package com.dickel.zeiterfassung

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dickel.zeiterfassung.data.local.AppDatabase
import com.dickel.zeiterfassung.data.local.entity.Customer
import com.dickel.zeiterfassung.data.local.entity.Serviceschein
import com.dickel.zeiterfassung.data.local.entity.TimeEvent
import com.dickel.zeiterfassung.data.repository.CustomerRepository
import com.dickel.zeiterfassung.data.repository.ServicescheinRepository
import com.dickel.zeiterfassung.data.repository.TimeEventRepository
import com.dickel.zeiterfassung.utils.PdfGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class WorkStatus {
    IDLE, ARBEIT, KUNDE, FAHRT, PAUSE, FEIERABEND
}

data class AppUiState(
    val currentStatus: WorkStatus = WorkStatus.IDLE,
    val customers: List<Customer> = emptyList(),
    val unsyncedEvents: List<TimeEvent> = emptyList(),
    val unsentServiceschein: List<Serviceschein> = emptyList(),
    val error: String? = null,
    val syncError: String? = null,
    val isSyncing: Boolean = false,
    val showCustomerDialog: Boolean = false,
    val showServicescheinPrompt: Boolean = false,
    val showServicescheinForm: Boolean = false,
    val showNfcFallbackDialog: Boolean = false,
    val selectedCustomer: Customer? = null,
    val benutzerId: String = "MA001"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getInstance(application)
    private val timeEventRepository = TimeEventRepository(database.timeEventDao())
    private val customerRepository = CustomerRepository(database.customerDao())
    private val servicescheinRepository = ServicescheinRepository(database.servicescheinDao())
    
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
        // Add some default customers
        viewModelScope.launch {
            val existingCustomers = customerRepository.getAllCustomers().first()
            if (existingCustomers.isEmpty()) {
                customerRepository.insertCustomer(Customer(name = "Kunde A", email = "kunde.a@example.com"))
                customerRepository.insertCustomer(Customer(name = "Kunde B", email = "kunde.b@example.com"))
                customerRepository.insertCustomer(Customer(name = "Kunde C", email = "kunde.c@example.com"))
            }
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                customerRepository.getAllCustomers(),
                timeEventRepository.getUnsyncedEvents(),
                servicescheinRepository.getUnsentServiceschein()
            ) { customers, unsynced, unsent ->
                _uiState.update {
                    it.copy(
                        customers = customers,
                        unsyncedEvents = unsynced,
                        unsentServiceschein = unsent
                    )
                }
            }.collect()
        }
        
        // Get last event to determine current status
        viewModelScope.launch {
            val lastEvent = timeEventRepository.getLastEvent()
            lastEvent?.let {
                val status = when (it.eventType) {
                    "ARBEIT" -> WorkStatus.ARBEIT
                    "KUNDE" -> WorkStatus.KUNDE
                    "FAHRT" -> WorkStatus.FAHRT
                    "PAUSE" -> WorkStatus.PAUSE
                    "FEIERABEND" -> WorkStatus.FEIERABEND
                    else -> WorkStatus.IDLE
                }
                _uiState.update { state -> state.copy(currentStatus = status) }
            }
        }
    }
    
    fun onArbeitszeitStarten() {
        recordEvent("ARBEIT")
        _uiState.update { it.copy(currentStatus = WorkStatus.ARBEIT) }
    }
    
    fun onKundenzeitStarten() {
        _uiState.update { it.copy(showCustomerDialog = true) }
    }
    
    fun onCustomerSelected(customer: Customer) {
        _uiState.update { it.copy(selectedCustomer = customer, showCustomerDialog = false) }
        recordEvent("KUNDE", customer.id, customer.name)
        _uiState.update { it.copy(currentStatus = WorkStatus.KUNDE) }
    }
    
    fun onKundenzeitBeenden() {
        recordEvent("KUNDE_END")
        _uiState.update { it.copy(showServicescheinPrompt = true) }
    }
    
    fun onServicescheinJetzt() {
        _uiState.update { it.copy(showServicescheinPrompt = false, showServicescheinForm = true) }
    }
    
    fun onServicescheinSpaeter() {
        _uiState.update { it.copy(showServicescheinPrompt = false, currentStatus = WorkStatus.ARBEIT) }
    }
    
    fun onFahrtStarten() {
        recordEvent("FAHRT")
        _uiState.update { it.copy(currentStatus = WorkStatus.FAHRT) }
    }
    
    fun onFahrtBeenden() {
        recordEvent("FAHRT_END")
        _uiState.update { it.copy(currentStatus = WorkStatus.ARBEIT) }
    }
    
    fun onPauseStarten() {
        recordEvent("PAUSE")
        _uiState.update { it.copy(currentStatus = WorkStatus.PAUSE) }
    }
    
    fun onPauseBeenden() {
        recordEvent("PAUSE_END")
        _uiState.update { it.copy(currentStatus = WorkStatus.ARBEIT) }
    }
    
    fun onFeierabend() {
        recordEvent("FEIERABEND")
        _uiState.update { it.copy(currentStatus = WorkStatus.FEIERABEND) }
    }
    
    fun onServicescheinSave(
        leistungen: String,
        zeiten: String,
        material: String,
        notizen: String,
        signatureData: String?
    ) {
        viewModelScope.launch {
            try {
                val customer = _uiState.value.selectedCustomer ?: return@launch
                
                // Generate PDF
                val pdfFile = PdfGenerator.generateServicescheinPdf(
                    context = getApplication(),
                    customerName = customer.name,
                    leistungen = leistungen,
                    zeiten = zeiten,
                    material = material,
                    notizen = notizen,
                    signatureBase64 = signatureData
                )
                
                // Save to database
                val serviceschein = Serviceschein(
                    customerId = customer.id,
                    customerName = customer.name,
                    leistungen = leistungen,
                    zeiten = zeiten,
                    material = material,
                    notizen = notizen,
                    signatureData = signatureData,
                    pdfPath = pdfFile.absolutePath,
                    createdAt = System.currentTimeMillis()
                )
                
                servicescheinRepository.insertServiceschein(serviceschein)
                
                // Try to send
                servicescheinRepository.sendServiceschein(serviceschein)
                
                _uiState.update { it.copy(showServicescheinForm = false, currentStatus = WorkStatus.ARBEIT) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun onSyncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncError = null) }
            try {
                // Sync events
                val unsynced = _uiState.value.unsyncedEvents
                for (event in unsynced) {
                    timeEventRepository.syncEvent(event)
                }
                
                // Sync serviceschein
                val unsent = _uiState.value.unsentServiceschein
                for (serviceschein in unsent) {
                    servicescheinRepository.sendServiceschein(serviceschein)
                }
                
                _uiState.update { it.copy(isSyncing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSyncing = false, syncError = e.message) }
            }
        }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun dismissSyncError() {
        _uiState.update { it.copy(syncError = null) }
    }
    
    fun showNfcFallback() {
        _uiState.update { it.copy(showNfcFallbackDialog = true) }
    }
    
    fun dismissNfcFallback() {
        _uiState.update { it.copy(showNfcFallbackDialog = false) }
    }
    
    fun dismissCustomerDialog() {
        _uiState.update { it.copy(showCustomerDialog = false) }
    }
    
    fun dismissServicescheinForm() {
        _uiState.update { it.copy(showServicescheinForm = false) }
    }
    
    private fun recordEvent(eventType: String, customerId: Long? = null, customerName: String? = null) {
        viewModelScope.launch {
            val event = TimeEvent(
                eventType = eventType,
                timestamp = System.currentTimeMillis(),
                customerId = customerId,
                customerName = customerName
            )
            timeEventRepository.insertEvent(event)
        }
    }
}
