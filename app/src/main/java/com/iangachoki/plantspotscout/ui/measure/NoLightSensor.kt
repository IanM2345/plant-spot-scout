package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.components.BackTopBar
import com.iangachoki.plantspotscout.ui.theme.Spacing

/** Shown instead of the gauge when the phone has no light sensor and demo mode is off. */
@Composable
fun NoLightSensor(
    spotName: String,
    onCancel: () -> Unit,
    onTurnOnDemo: () -> Unit,
    onPickBand: (LightBand) -> Unit,
    modifier: Modifier = Modifier
) {
    var picking by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BackTopBar(title = spotName, subtitle = stringResource(R.string.measuring_subtitle), onBack = onCancel) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(onClick = onTurnOnDemo, modifier = Modifier.fillMaxWidth().height(Spacing.buttonHeight)) {
                    Text(stringResource(R.string.turn_on_demo))
                }
                OutlinedButton(onClick = { picking = true }, modifier = Modifier.fillMaxWidth().height(Spacing.buttonHeight)) {
                    Text(stringResource(R.string.enter_by_hand))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Icon(
                painterResource(R.drawable.ic_no_sensor),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Text(
                stringResource(R.string.no_sensor_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.no_sensor_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            InfoCard(R.string.try_demo_title, R.string.try_demo_body)
            InfoCard(R.string.enter_by_hand, R.string.enter_by_hand_body)
        }
    }

    if (picking) {
        AlertDialog(
            onDismissRequest = { picking = false },
            title = { Text(stringResource(R.string.enter_by_hand)) },
            text = {
                Column {
                    LightBand.entries.forEach { band ->
                        TextButton(onClick = { onPickBand(band) }, modifier = Modifier.fillMaxWidth()) {
                            Text(band.label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { picking = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun InfoCard(title: Int, body: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
