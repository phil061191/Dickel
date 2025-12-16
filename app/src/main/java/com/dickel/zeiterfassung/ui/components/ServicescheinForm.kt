package com.dickel.zeiterfassung.ui.components

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dickel.zeiterfassung.R
import java.io.ByteArrayOutputStream

data class PathData(val path: Path, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicescheinFormDialog(
    customerName: String,
    onSave: (leistungen: String, zeiten: String, material: String, notizen: String, signatureData: String?) -> Unit,
    onDismiss: () -> Unit,
    onSpeechToText: (field: String, onResult: (String) -> Unit) -> Unit
) {
    var leistungen by remember { mutableStateOf("") }
    var zeiten by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var notizen by remember { mutableStateOf("") }
    var paths by remember { mutableStateOf(listOf<PathData>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.serviceschein_erstellen),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Scrollable form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "${stringResource(R.string.field_customer)}: $customerName")
                    
                    // Leistungen field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = leistungen,
                            onValueChange = { leistungen = it },
                            label = { Text(stringResource(R.string.field_leistungen)) },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { 
                            onSpeechToText("leistungen") { result -> leistungen = result }
                        }) {
                            Icon(Icons.Default.Mic, contentDescription = "Speech to text")
                        }
                    }
                    
                    // Zeiten field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = zeiten,
                            onValueChange = { zeiten = it },
                            label = { Text(stringResource(R.string.field_zeiten)) },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { 
                            onSpeechToText("zeiten") { result -> zeiten = result }
                        }) {
                            Icon(Icons.Default.Mic, contentDescription = "Speech to text")
                        }
                    }
                    
                    // Material field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = material,
                            onValueChange = { material = it },
                            label = { Text(stringResource(R.string.field_material)) },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { 
                            onSpeechToText("material") { result -> material = result }
                        }) {
                            Icon(Icons.Default.Mic, contentDescription = "Speech to text")
                        }
                    }
                    
                    // Notizen field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = notizen,
                            onValueChange = { notizen = it },
                            label = { Text(stringResource(R.string.field_notizen)) },
                            modifier = Modifier.weight(1f),
                            minLines = 3
                        )
                        IconButton(onClick = { 
                            onSpeechToText("notizen") { result -> notizen = result }
                        }) {
                            Icon(Icons.Default.Mic, contentDescription = "Speech to text")
                        }
                    }
                    
                    // Signature pad
                    Text(
                        text = stringResource(R.string.field_signature),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .border(1.dp, Color.Gray)
                            .background(Color.White)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        currentPath?.lineTo(change.position.x, change.position.y)
                                    },
                                    onDragEnd = {
                                        currentPath?.let {
                                            paths = paths + PathData(it, Color.Black)
                                            currentPath = null
                                        }
                                    }
                                )
                            }
                    ) {
                        paths.forEach { pathData ->
                            drawPath(
                                path = pathData.path,
                                color = pathData.color,
                                style = Stroke(
                                    width = 5f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                        currentPath?.let {
                            drawPath(
                                path = it,
                                color = Color.Black,
                                style = Stroke(
                                    width = 5f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                    
                    TextButton(onClick = { paths = emptyList() }) {
                        Text(stringResource(R.string.field_clear_signature))
                    }
                }
                
                // Save button
                Button(
                    onClick = {
                        // Convert signature to base64 (simplified - would need actual bitmap conversion)
                        val signatureData = if (paths.isNotEmpty()) "signature_placeholder" else null
                        onSave(leistungen, zeiten, material, notizen, signatureData)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.field_save))
                }
            }
        }
    }
}
