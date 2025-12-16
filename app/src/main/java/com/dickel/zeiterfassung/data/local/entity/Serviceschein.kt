package com.dickel.zeiterfassung.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serviceschein")
data class Serviceschein(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val leistungen: String,
    val zeiten: String,
    val material: String,
    val notizen: String,
    val signatureData: String?, // Base64 encoded signature image
    val pdfPath: String? = null,
    val createdAt: Long,
    val isSent: Boolean = false,
    val sendError: String? = null
)
