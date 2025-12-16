package com.dickel.zeiterfassung.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dickel.zeiterfassung.R
import com.dickel.zeiterfassung.WorkStatus

@Composable
fun ActionButtons(
    currentStatus: WorkStatus,
    onArbeitszeitStarten: () -> Unit,
    onKundenzeitStarten: () -> Unit,
    onKundenzeitBeenden: () -> Unit,
    onFahrtStarten: () -> Unit,
    onFahrtBeenden: () -> Unit,
    onPauseStarten: () -> Unit,
    onPauseBeenden: () -> Unit,
    onFeierabend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (currentStatus) {
            WorkStatus.IDLE -> {
                Button(
                    onClick = onArbeitszeitStarten,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_arbeitszeit_starten))
                }
            }
            WorkStatus.ARBEIT -> {
                Button(
                    onClick = onKundenzeitStarten,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_kundenzeit_starten))
                }
                Button(
                    onClick = onFahrtStarten,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_fahrt_starten))
                }
                Button(
                    onClick = onPauseStarten,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_pause_starten))
                }
                Button(
                    onClick = onFeierabend,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.btn_feierabend))
                }
            }
            WorkStatus.KUNDE -> {
                Button(
                    onClick = onKundenzeitBeenden,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_kundenzeit_beenden))
                }
                Button(
                    onClick = onFeierabend,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.btn_feierabend))
                }
            }
            WorkStatus.FAHRT -> {
                Button(
                    onClick = onFahrtBeenden,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_fahrt_beenden))
                }
                Button(
                    onClick = onFeierabend,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.btn_feierabend))
                }
            }
            WorkStatus.PAUSE -> {
                Button(
                    onClick = onPauseBeenden,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_pause_beenden))
                }
                Button(
                    onClick = onFeierabend,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.btn_feierabend))
                }
            }
            WorkStatus.FEIERABEND -> {
                // No buttons shown after Feierabend
                Text(
                    text = "Feierabend - App kann geschlossen werden",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
