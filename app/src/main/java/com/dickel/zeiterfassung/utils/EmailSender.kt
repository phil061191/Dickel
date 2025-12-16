package com.dickel.zeiterfassung.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.Properties
import javax.mail.*
import javax.mail.internet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EmailSender {
    
    suspend fun sendServicescheinEmail(
        context: Context,
        recipientEmail: String,
        pdfFile: File,
        customerName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // For now, use intent to open email client
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, "Serviceschein - $customerName")
                putExtra(Intent.EXTRA_TEXT, "Anbei finden Sie den Serviceschein.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(Intent.createChooser(intent, "Email senden").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
