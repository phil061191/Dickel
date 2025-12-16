package com.dickel.zeiterfassung.data.remote.model

data class SyncRequest(
    val eventType: String,
    val timestamp: Long,
    val customerId: Long? = null,
    val customerName: String? = null
)

data class SyncResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)
