package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import com.iangachoki.plantspotscout.ui.components.LightBandChip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onDeleted: () -> Unit,
    onMeasure: (Spot) -> Unit,
    onTakePhoto: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {
    val spots by viewModel.spots.collectAsState()
    val plants by viewModel.plants.collectAsState()
    val spot = spots.firstOrNull { it.id == spotId }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.spot_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (spot != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.edit))
                        }
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
                onDelete = { showDeleteDialog = true },
                onMeasure = { onMeasure(spot) },
                onTakePhoto = { onTakePhoto(spot) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showEditDialog && spot != null) {
        AddSpotDialog(
            rooms = viewModel.rooms,
            title = stringResource(R.string.edit_spot),
            confirmLabel = stringResource(R.string.save),
            initialName = spot.name,
            initialRoom = spot.room,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, room ->
                viewModel.updateSpot(spot.id, name, room)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && spot != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_spot_title)) },
            text = { Text(stringResource(R.string.delete_spot_message, spot.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSpot(spot.id)
                        onDeleted()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SpotDetails(
    spot: Spot,
    plants: List<Plant>,
    onDelete: () -> Unit,
    onMeasure: () -> Unit,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SpotPhoto(
            spot.photoUri,
            size = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        OutlinedButton(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) {
            Icon(
                painterResource(R.drawable.ic_photo),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                stringResource(if (spot.photoUri == null) R.string.take_photo else R.string.retake_photo)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(spot.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                spot.room,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LightBandChip(spot.lightBand)
        Button(
            onClick = onMeasure,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(stringResource(R.string.measure), style = MaterialTheme.typography.titleMedium)
        }

        Text(
            stringResource(R.string.plants_here),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (plants.isEmpty()) {
            Text(
                stringResource(R.string.no_plants_here),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                plants.forEachIndexed { index, plant ->
                    if (index > 0) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    PlantRow(plant)
                }
            }
        }

        OutlinedButton(
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(stringResource(R.string.delete_spot))
        }
    }
}

@Composable
private fun PlantRow(plant: Plant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_plant),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(plant.nickname, style = MaterialTheme.typography.titleMedium)
            Text(
                plant.type.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
