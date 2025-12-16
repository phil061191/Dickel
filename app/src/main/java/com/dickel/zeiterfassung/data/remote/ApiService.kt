package com.dickel.zeiterfassung.data.remote

import com.dickel.zeiterfassung.data.remote.model.SyncRequest
import com.dickel.zeiterfassung.data.remote.model.SyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    
    @POST("sync-event")
    suspend fun syncEvent(@Body request: SyncRequest): Response<SyncResponse>
    
    @POST("send-serviceschein")
    suspend fun sendServiceschein(@Body request: Map<String, Any>): Response<SyncResponse>
    
    @GET("customers")
    suspend fun getCustomers(): Response<List<Map<String, Any>>>
}
