package com.dickel.zeiterfassung.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_events")
data class TimeEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventType: String, // ARBEIT, KUNDE, FAHRT, PAUSE, FEIERABEND
    val timestamp: Long,
    val customerId: Long? = null,
    val customerName: String? = null,
    val isSynced: Boolean = false,
    val syncError: String? = null
)
