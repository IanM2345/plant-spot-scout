package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.SpotDetailActivity
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.components.EmptyState
import com.iangachoki.plantspotscout.ui.components.LightBandChip
import com.iangachoki.plantspotscout.ui.components.ScreenHeader
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

@Composable
fun MySpotsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val spots by viewModel.spots.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        SpotList(
            spots = spots,
            onSpotClick = { spot ->
                context.startActivity(SpotDetailActivity.newIntent(context, spot.id))
            }
        )
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = null) },
            text = { Text(stringResource(R.string.add_spot)) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddSpotDialog(
            rooms = viewModel.rooms,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, room ->
                viewModel.addSpot(name, room)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SpotList(
    spots: List<Spot>,
    onSpotClick: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            title = stringResource(R.string.tab_my_spots),
            subtitle = stringResource(R.string.my_spots_subtitle),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (spots.isEmpty()) {
            EmptyState(R.drawable.ic_spot, stringResource(R.string.no_spot))
            return@Column
        }
        LazyColumn(
            // Extra bottom padding so the add button never covers the last card.
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(spots, key = { it.id }) { spot ->
                SpotCard(spot, onClick = { onSpotClick(spot) })
            }
        }
    }
}

@Composable
fun SpotCard(spot: Spot, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpotPhoto(spot.photoUri)
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(spot.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    spot.room,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LightBandChip(spot.lightBand, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpotListPreview() {
    PlantSpotScoutTheme {
        SpotList(
            listOf(
                Spot(1, "Window sill", "Kitchen", LightBand.DIRECT),
                Spot(2, "Bookshelf corner", "Living room", LightBand.MEDIUM)
            ),
            onSpotClick = {}
        )
    }
}
