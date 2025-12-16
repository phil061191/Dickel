package com.dickel.zeiterfassung.data.repository

import com.dickel.zeiterfassung.data.local.dao.ServicescheinDao
import com.dickel.zeiterfassung.data.local.entity.Serviceschein
import com.dickel.zeiterfassung.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class ServicescheinRepository(private val dao: ServicescheinDao) {
    
    fun getAllServiceschein(): Flow<List<Serviceschein>> = dao.getAllServiceschein()
    
    fun getUnsentServiceschein(): Flow<List<Serviceschein>> = dao.getUnsentServiceschein()
    
    suspend fun getServicescheinById(id: Long): Serviceschein? = dao.getServicescheinById(id)
    
    suspend fun insertServiceschein(serviceschein: Serviceschein): Long = dao.insert(serviceschein)
    
    suspend fun updateServiceschein(serviceschein: Serviceschein) = dao.update(serviceschein)
    
    suspend fun deleteServiceschein(serviceschein: Serviceschein) = dao.delete(serviceschein)
    
    suspend fun sendServiceschein(serviceschein: Serviceschein): Result<Unit> {
        return try {
            val request = mapOf(
                "id" to serviceschein.id,
                "customerId" to serviceschein.customerId,
                "customerName" to serviceschein.customerName,
                "leistungen" to serviceschein.leistungen,
                "zeiten" to serviceschein.zeiten,
                "material" to serviceschein.material,
                "notizen" to serviceschein.notizen,
                "signatureData" to (serviceschein.signatureData ?: ""),
                "pdfPath" to (serviceschein.pdfPath ?: ""),
                "createdAt" to serviceschein.createdAt
            )
            
            val response = RetrofitClient.apiService.sendServiceschein(request)
            if (response.isSuccessful && response.body()?.success == true) {
                dao.markAsSent(serviceschein.id)
                Result.success(Unit)
            } else {
                val error = response.body()?.error ?: "Unknown error"
                dao.setSendError(serviceschein.id, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            dao.setSendError(serviceschein.id, e.message ?: "Network error")
            Result.failure(e)
        }
    }
}
