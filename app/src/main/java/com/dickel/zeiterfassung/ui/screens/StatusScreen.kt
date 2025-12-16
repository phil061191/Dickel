package com.dickel.zeiterfassung.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dickel.zeiterfassung.AppUiState
import com.dickel.zeiterfassung.MainViewModel
import com.dickel.zeiterfassung.R
import com.dickel.zeiterfassung.ui.components.*
import com.dickel.zeiterfassung.utils.SpeechToTextHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    viewModel: MainViewModel,
    uiState: AppUiState,
    speechToTextHelper: SpeechToTextHelper,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Error banner
        if (uiState.error != null) {
            ErrorBanner(
                message = uiState.error,
                onDismiss = { viewModel.dismissError() }
            )
        }
        
        // Sync error banner
        if (uiState.syncError != null) {
            SyncErrorBanner(
                message = uiState.syncError,
                onRetry = { viewModel.onSyncNow() },
                onDismiss = { viewModel.dismissSyncError() }
            )
        }
        
        // Pending events banner
        val pendingCount = uiState.unsyncedEvents.size + uiState.unsentServiceschein.size
        if (pendingCount > 0) {
            PendingEventsBanner(
                count = pendingCount,
                isSyncing = uiState.isSyncing,
                onSync = { viewModel.onSyncNow() }
            )
        }
        
        // Ampel card
        AmpelCard(currentStatus = uiState.currentStatus)
        
        // Action buttons
        ActionButtons(
            currentStatus = uiState.currentStatus,
            onArbeitszeitStarten = { viewModel.onArbeitszeitStarten() },
            onKundenzeitStarten = { viewModel.onKundenzeitStarten() },
            onKundenzeitBeenden = { viewModel.onKundenzeitBeenden() },
            onFahrtStarten = { viewModel.onFahrtStarten() },
            onFahrtBeenden = { viewModel.onFahrtBeenden() },
            onPauseStarten = { viewModel.onPauseStarten() },
            onPauseBeenden = { viewModel.onPauseBeenden() },
            onFeierabend = { viewModel.onFeierabend() }
        )
        
        // MA-Übersicht
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.ma_overview),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(R.string.benutzer_id)}: ${uiState.benutzerId}")
            }
        }
        
        // NFC Fallback button
        Button(
            onClick = { viewModel.showNfcFallback() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("NFC Fallback")
        }
    }
    
    // Dialogs
    if (uiState.showCustomerDialog) {
        CustomerSelectionDialog(
            customers = uiState.customers,
            onCustomerSelected = { viewModel.onCustomerSelected(it) },
            onDismiss = { viewModel.dismissCustomerDialog() }
        )
    }
    
    if (uiState.showServicescheinPrompt) {
        ServicescheinPromptDialog(
            onJetzt = { viewModel.onServicescheinJetzt() },
            onSpaeter = { viewModel.onServicescheinSpaeter() },
            onDismiss = { viewModel.onServicescheinSpaeter() }
        )
    }
    
    if (uiState.showServicescheinForm) {
        var speechResultCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }
        
        LaunchedEffect(speechResultCallback) {
            if (speechResultCallback != null) {
                speechToTextHelper.startListening().collect { result ->
                    speechResultCallback?.invoke(result)
                    speechResultCallback = null
                }
            }
        }
        
        ServicescheinFormDialog(
            customerName = uiState.selectedCustomer?.name ?: "",
            onSave = { leistungen, zeiten, material, notizen, signature ->
                viewModel.onServicescheinSave(leistungen, zeiten, material, notizen, signature)
            },
            onDismiss = { viewModel.dismissServicescheinForm() },
            onSpeechToText = { field, callback ->
                speechResultCallback = callback
            }
        )
    }
    
    if (uiState.showNfcFallbackDialog) {
        NfcFallbackDialog(
            onAutoSelected = {
                // Handle Auto (Abfahrt zum Kunden)
                viewModel.onKundenzeitStarten()
            },
            onFirmaSelected = {
                // Handle Firma (Einstempeln)
                viewModel.onArbeitszeitStarten()
            },
            onDismiss = { viewModel.dismissNfcFallback() }
        )
    }
}
