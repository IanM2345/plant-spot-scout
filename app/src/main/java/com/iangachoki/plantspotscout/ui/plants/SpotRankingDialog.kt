package com.iangachoki.plantspotscout.ui.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.Spot

/**
 * Lists every spot for [plant], best match first.
 */
@Composable
fun SpotRankingDialog(
    plant: Plant,
    rankedSpots: List<Spot>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ranking_title, plant.nickname)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stringResource(R.string.ranking_subtitle, plant.type.lightNeeds.label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (rankedSpots.isEmpty()) {
                    Text(stringResource(R.string.no_spots_to_rank))
                }
                rankedSpots.forEachIndexed { index, spot ->
                    RankedSpotRow(position = index + 1, spot = spot, plant = plant)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun RankedSpotRow(position: Int, spot: Spot, plant: Plant) {
    val fit = LightFit.of(plant.type.lightNeeds, spot.lightBand)
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$position.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(spot.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                "${spot.room} · ${spot.lightBand.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            fit.label,
            style = MaterialTheme.typography.labelLarge,
            color = if (fit == LightFit.GOOD) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
