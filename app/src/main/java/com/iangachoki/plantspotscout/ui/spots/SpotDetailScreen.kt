package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotDetailScreen(
    spotId: Long,
    viewModel: SpotsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spots by viewModel.spots.collectAsState()
    val plants by viewModel.plants.collectAsState()
    val spot = spots.firstOrNull { it.id == spotId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(spot?.name ?: stringResource(R.string.spot_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (spot == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.spot_not_found))
            }
        } else {
            SpotDetails(
                spot = spot,
                plants = plants.filter { it.currentSpotId == spot.id },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun SpotDetails(spot: Spot, plants: List<Plant>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SpotPhotoPlaceholder()
        Text(spot.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            spot.room,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AssistChip(
            onClick = {},
            label = { Text(stringResource(R.string.light_band, spot.lightBand.label)) }
        )

        HorizontalDivider()

        Text(stringResource(R.string.plants_here), style = MaterialTheme.typography.titleMedium)
        if (plants.isEmpty()) {
            Text(
                stringResource(R.string.no_plants_here),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            plants.forEach { plant ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(plant.nickname, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        plant.type.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
