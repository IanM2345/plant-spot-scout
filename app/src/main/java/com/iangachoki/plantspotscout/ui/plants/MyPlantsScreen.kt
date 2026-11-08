package com.iangachoki.plantspotscout.ui.plants

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.components.EmptyState
import com.iangachoki.plantspotscout.ui.components.FitBadge
import com.iangachoki.plantspotscout.ui.components.ScreenHeader
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

@Composable
fun MyPlantsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val plants by viewModel.plants.collectAsState()
    val spots by viewModel.spots.collectAsState()

    var rankingFor by rememberSaveable { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    PlantList(
        plants = plants,
        spots = spots,
        onWhereShouldThisGo = { rankingFor = it.id },
        onOpenCarePage = { plant ->
            val view = Intent(Intent.ACTION_VIEW, Uri.parse(plant.type.careUrl))
            try {
                context.startActivity(view)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, R.string.no_browser, Toast.LENGTH_SHORT).show()
            }
        },
        onShare = { plant ->
            val text = bestSpotMessage(context, plant, viewModel.rankSpotsFor(plant).firstOrNull())
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_subject, plant.nickname))
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(send, context.getString(R.string.share_plant)))
        },
        modifier = modifier
    )

    val plant = plants.firstOrNull { it.id == rankingFor }
    if (plant != null) {
        SpotRankingDialog(
            plant = plant,
            rankedSpots = viewModel.rankSpotsFor(plant),
            onDismiss = { rankingFor = null }
        )
    }
}

@Composable
private fun PlantList(
    plants: List<Plant>,
    spots: List<Spot>,
    onWhereShouldThisGo: (Plant) -> Unit,
    onOpenCarePage: (Plant) -> Unit,
    onShare: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            title = stringResource(R.string.tab_my_plants),
            subtitle = stringResource(R.string.my_plants_subtitle),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (plants.isEmpty()) {
            EmptyState(R.drawable.ic_plant, stringResource(R.string.no_plants))
            return@Column
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plants, key = { it.id }) { plant ->
                PlantCard(
                    plant = plant,
                    spot = spots.firstOrNull { it.id == plant.currentSpotId },
                    onWhereShouldThisGo = { onWhereShouldThisGo(plant) },
                    onOpenCarePage = { onOpenCarePage(plant) },
                    onShare = { onShare(plant) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlantCard(
    plant: Plant,
    spot: Spot?,
    onWhereShouldThisGo: () -> Unit,
    onOpenCarePage: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
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
                        .padding(start = 12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            plant.nickname,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (spot != null) {
                            FitBadge(LightFit.of(plant.type.lightNeeds, spot.lightBand))
                        }
                    }
                    Text(
                        stringResource(R.string.plant_needs, plant.type.name, plant.type.lightNeeds.label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        spot?.let { stringResource(R.string.plant_in_spot, it.name, it.room) }
                            ?: stringResource(R.string.no_spot),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (spot == null) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(onClick = onWhereShouldThisGo) {
                    Text(stringResource(R.string.where_should_this_go))
                }
                TextButton(onClick = onShare) {
                    Icon(
                        painterResource(R.drawable.ic_share),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.share_plant))
                }
                TextButton(onClick = onOpenCarePage) {
                    Text(stringResource(R.string.care_page))
                }
            }
        }
    }
}

/** Text for sharing where [plant] does best. [bestSpot] is null when there are no spots. */
private fun bestSpotMessage(context: Context, plant: Plant, bestSpot: Spot?): String =
    if (bestSpot == null) {
        context.getString(R.string.share_no_spot, plant.nickname, plant.type.name, plant.type.lightNeeds.label)
    } else {
        context.getString(
            R.string.share_best_spot,
            plant.nickname,
            plant.type.name,
            bestSpot.name,
            bestSpot.room,
            bestSpot.lightBand.label
        )
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
            spots = listOf(spot),
            onWhereShouldThisGo = {},
            onOpenCarePage = {},
            onShare = {}
        )
    }
}
