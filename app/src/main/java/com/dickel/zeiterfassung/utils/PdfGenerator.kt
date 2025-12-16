package com.dickel.zeiterfassung.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.io.image.ImageDataFactory
import java.io.ByteArrayOutputStream
import java.io.File

object PdfGenerator {
    
    fun generateServicescheinPdf(
        context: Context,
        customerName: String,
        leistungen: String,
        zeiten: String,
        material: String,
        notizen: String,
        signatureBase64: String?
    ): File {
        val fileName = "serviceschein_${System.currentTimeMillis()}.pdf"
        val file = File(context.filesDir, fileName)
        
        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)
        
        // Add content
        document.add(Paragraph("Serviceschein").setFontSize(20f).setBold())
        document.add(Paragraph("\n"))
        document.add(Paragraph("Kunde: $customerName"))
        document.add(Paragraph("\n"))
        document.add(Paragraph("Leistungen:").setBold())
        document.add(Paragraph(leistungen))
        document.add(Paragraph("\n"))
        document.add(Paragraph("Zeiten:").setBold())
        document.add(Paragraph(zeiten))
        document.add(Paragraph("\n"))
        document.add(Paragraph("Material:").setBold())
        document.add(Paragraph(material))
        document.add(Paragraph("\n"))
        document.add(Paragraph("Notizen:").setBold())
        document.add(Paragraph(notizen))
        document.add(Paragraph("\n"))
        
        // Add signature if available
        if (!signatureBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(signatureBase64, Base64.DEFAULT)
                val imageData = ImageDataFactory.create(imageBytes)
                val image = Image(imageData).scaleToFit(200f, 100f)
                document.add(Paragraph("Unterschrift:").setBold())
                document.add(image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        document.close()
        return file
    }
}
