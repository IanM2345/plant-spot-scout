package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import com.iangachoki.plantspotscout.ui.components.PhotoHeaderAction
import com.iangachoki.plantspotscout.ui.components.PhotoHeaderTopBar
import com.iangachoki.plantspotscout.ui.theme.Spacing
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.model.bandDistance
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel

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
    val spot = spots.firstOrNull { it.id == spotId }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        if (spot == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.spot_not_found))
            }
            PhotoHeaderTopBar(onBack = onBack)
        } else {
            SpotDetails(
                spot = spot,
                plantTypes = viewModel.plantTypes,
                onEdit = { showEditDialog = true },
                onMeasure = { onMeasure(spot) }
            )
            // Drawn over the photo at the top of the screen.
            PhotoHeaderTopBar(onBack = onBack) {
                PhotoHeaderAction(
                    icon = R.drawable.ic_camera,
                    contentDescription = stringResource(
                        if (spot.photoUri == null) R.string.take_photo else R.string.retake_photo
                    ),
                    onClick = { onTakePhoto(spot) }
                )
                PhotoHeaderAction(
                    icon = R.drawable.ic_share,
                    contentDescription = stringResource(R.string.share_spot),
                    onClick = { shareSpot(context, spot) }
                )
                Box {
                    PhotoHeaderAction(
                        icon = R.drawable.ic_more_vert,
                        contentDescription = stringResource(R.string.more_options),
                        onClick = { showMenu = true }
                    )
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit_spot)) },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_edit), contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showEditDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete_spot)) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
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
    plantTypes: List<PlantType>,
    onEdit: () -> Unit,
    onMeasure: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The photo runs up behind the status bar, so it gets that much extra height.
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpotPhoto(
            spot.photoUri,
            size = null,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp + statusBarHeight)
        )
        Column(
            modifier = Modifier.padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Text(spot.name, style = MaterialTheme.typography.headlineMedium)
        LightBandChip(spot.lightBand)

        Text(
            stringResource(R.string.best_plants),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = Spacing.sectionGap)
        )
        // Closest band first: distance 0 is exactly the GOOD fits, then the nearest misses.
        val best = plantTypes
            .map { it to LightFit.of(it.lightNeeds, spot.lightBand) }
            .sortedBy { bandDistance(it.first.lightNeeds, spot.lightBand) }
            .take(3)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            best.forEachIndexed { index, (type, fit) ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                FitRow(index + 1, type, fit)
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sectionGap, bottom = Spacing.lg)
        ) {
            Button(onClick = onMeasure, modifier = Modifier.weight(1f).height(Spacing.buttonHeight)) {
                Text(stringResource(R.string.measure_again))
            }
            OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f).height(Spacing.buttonHeight)) {
                Text(stringResource(R.string.edit_spot))
            }
        }
        }
    }
}

/** Opens the share sheet with a short note about the spot and its light. */
private fun shareSpot(context: Context, spot: Spot) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.share_spot_text, spot.name, spot.room, spot.lightBand.label)
        )
    }
    context.startActivity(Intent.createChooser(send, context.getString(R.string.share_spot)))
}

@Composable
private fun FitRow(rank: Int, type: PlantType, fit: LightFit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.cardPadding, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(rank.toString(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = Spacing.md)) {
            Text(type.name, style = MaterialTheme.typography.titleMedium)
            Text(
                when (fit) {
                    LightFit.GOOD -> stringResource(R.string.fit_reason_good, type.lightNeeds.label.lowercase())
                    LightFit.TOO_DARK -> stringResource(R.string.fit_reason_dark)
                    LightFit.TOO_BRIGHT -> stringResource(R.string.fit_reason_bright)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        val good = fit == LightFit.GOOD
        Text(
            stringResource(if (good) R.string.great_fit else R.string.okay_fit),
            color = if (good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
