package com.iangachoki.plantspotscout.ui.spots

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
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
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.add_spot))
        }
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
    LazyColumn(
        modifier = modifier,
        // Extra bottom padding so the add button never covers the last card.
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(spots, key = { it.id }) { spot ->
            SpotCard(spot, onClick = { onSpotClick(spot) })
        }
    }
}

@Composable
fun SpotCard(spot: Spot, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpotPhotoPlaceholder()
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(spot.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    spot.room,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.light_band, spot.lightBand.label)) }
                )
            }
        }
    }
}

@Composable
internal fun SpotPhotoPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_photo),
            contentDescription = stringResource(R.string.spot_photo_placeholder),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
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
