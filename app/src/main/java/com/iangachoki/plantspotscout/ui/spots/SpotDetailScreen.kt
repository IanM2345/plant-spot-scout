package com.iangachoki.plantspotscout.ui.spots

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
import androidx.compose.material3.AssistChip
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
                onEdit = { showEditDialog = true },
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
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMeasure: () -> Unit,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SpotPhoto(spot.photoUri, size = 160.dp)
        OutlinedButton(onClick = onTakePhoto) {
            Text(
                stringResource(if (spot.photoUri == null) R.string.take_photo else R.string.retake_photo)
            )
        }
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
        Button(onClick = onMeasure, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.measure))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onEdit) {
                Text(stringResource(R.string.edit))
            }
            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete))
            }
        }

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
