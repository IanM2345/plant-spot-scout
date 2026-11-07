package com.iangachoki.plantspotscout.ui.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

@Composable
fun MyPlantsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val plants by viewModel.plants.collectAsState()
    val spots by viewModel.spots.collectAsState()

    PlantList(plants = plants, spots = spots, modifier = modifier)
}

@Composable
private fun PlantList(plants: List<Plant>, spots: List<Spot>, modifier: Modifier = Modifier) {
    if (plants.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_plants))
        }
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(plants, key = { it.id }) { plant ->
            PlantCard(plant = plant, spot = spots.firstOrNull { it.id == plant.currentSpotId })
        }
    }
}

@Composable
private fun PlantCard(plant: Plant, spot: Spot?, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_plant),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(plant.nickname, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.plant_needs, plant.type.name, plant.type.lightNeeds.label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    spot?.let { stringResource(R.string.plant_in_spot, it.name, it.room) }
                        ?: stringResource(R.string.no_spot),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (spot != null) {
                    FitChip(LightFit.of(plant.type.lightNeeds, spot.lightBand))
                }
            }
        }
    }
}

@Composable
private fun FitChip(fit: LightFit) {
    val colors = if (fit == LightFit.GOOD) {
        AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    } else {
        AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
    AssistChip(onClick = {}, label = { Text(fit.label) }, colors = colors)
}

@Preview(showBackground = true)
@Composable
private fun PlantListPreview() {
    val spot = Spot(1, "Window sill", "Kitchen", LightBand.DIRECT)
    PlantSpotScoutTheme {
        PlantList(
            plants = listOf(
                Plant(1, "Spike", PlantType("Aloe vera", LightBand.DIRECT), currentSpotId = 1),
                Plant(2, "Lily", PlantType("Peace lily", LightBand.LOW))
            ),
            spots = listOf(spot)
        )
    }
}
