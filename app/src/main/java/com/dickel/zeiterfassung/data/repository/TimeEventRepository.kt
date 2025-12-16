package com.dickel.zeiterfassung.data.repository

import com.dickel.zeiterfassung.data.local.dao.TimeEventDao
import com.dickel.zeiterfassung.data.local.entity.TimeEvent
import com.dickel.zeiterfassung.data.remote.RetrofitClient
import com.dickel.zeiterfassung.data.remote.model.SyncRequest
import kotlinx.coroutines.flow.Flow

class TimeEventRepository(private val dao: TimeEventDao) {
    
    fun getAllEvents(): Flow<List<TimeEvent>> = dao.getAllEvents()
    
    fun getUnsyncedEvents(): Flow<List<TimeEvent>> = dao.getUnsyncedEvents()
    
    suspend fun getLastEvent(): TimeEvent? = dao.getLastEvent()
    
    suspend fun insertEvent(event: TimeEvent): Long = dao.insert(event)
    
    suspend fun updateEvent(event: TimeEvent) = dao.update(event)
    
    suspend fun deleteEvent(event: TimeEvent) = dao.delete(event)
    
    suspend fun syncEvent(event: TimeEvent): Result<Unit> {
        return try {
            val request = SyncRequest(
                eventType = event.eventType,
                timestamp = event.timestamp,
                customerId = event.customerId,
                customerName = event.customerName
            )
            val response = RetrofitClient.apiService.syncEvent(request)
            if (response.isSuccessful && response.body()?.success == true) {
                dao.markAsSynced(event.id)
                Result.success(Unit)
            } else {
                val error = response.body()?.error ?: "Unknown error"
                dao.setSyncError(event.id, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            dao.setSyncError(event.id, e.message ?: "Network error")
            Result.failure(e)
        }
    }
    
    suspend fun syncAllPending() {
        val unsynced = dao.getUnsyncedEvents()
        // Note: Flow, so we'd need to collect - simplified for now
    }
}
