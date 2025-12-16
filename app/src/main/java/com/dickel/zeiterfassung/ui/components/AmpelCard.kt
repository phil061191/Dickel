package com.dickel.zeiterfassung.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dickel.zeiterfassung.WorkStatus
import com.dickel.zeiterfassung.ui.theme.*

@Composable
fun AmpelCard(
    currentStatus: WorkStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ampel lights in a row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                AmpelLight(
                    label = "Arbeit",
                    color = AmpelGreen,
                    isActive = currentStatus == WorkStatus.ARBEIT
                )
                AmpelLight(
                    label = "Kunde",
                    color = AmpelBlue,
                    isActive = currentStatus == WorkStatus.KUNDE
                )
                AmpelLight(
                    label = "Fahrt",
                    color = AmpelCyan,
                    isActive = currentStatus == WorkStatus.FAHRT
                )
                AmpelLight(
                    label = "Pause",
                    color = AmpelYellow,
                    isActive = currentStatus == WorkStatus.PAUSE
                )
                AmpelLight(
                    label = "Feierabend",
                    color = AmpelRed,
                    isActive = currentStatus == WorkStatus.FEIERABEND
                )
            }
        }
    }
}

@Composable
fun AmpelLight(
    label: String,
    color: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) color else AmpelGrey)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
