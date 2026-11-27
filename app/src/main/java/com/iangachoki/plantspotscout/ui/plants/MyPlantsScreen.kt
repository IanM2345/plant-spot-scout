package com.iangachoki.plantspotscout.ui.plants

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.FileProvider
import com.iangachoki.plantspotscout.BuildConfig
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.data.identifyPlant
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.PlantMatch
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.components.EmptyState
import com.iangachoki.plantspotscout.ui.components.FitBadge
import com.iangachoki.plantspotscout.ui.components.LargeTitleTopBar
import com.iangachoki.plantspotscout.ui.components.TopBarIconButton
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MyPlantsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val plants by viewModel.plants.collectAsState()
    val spots by viewModel.spots.collectAsState()

    var rankingFor by rememberSaveable { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    var identifying by remember { mutableStateOf(false) }
    var matches by remember { mutableStateOf<List<PlantMatch>?>(null) }
    var identifiedType by remember { mutableStateOf<PlantType?>(null) }
    val scope = rememberCoroutineScope()
    // Full-size photo in a cache file (unlike Spot detail's thumbnail); identifyPlant deletes it.
    var photoFile by remember { mutableStateOf<File?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { taken ->
        val file = photoFile
        if (taken && file != null) scope.launch {
            identifying = true
            val found = identifyPlant(file)
            identifying = false
            if (found == null) Toast.makeText(context, R.string.identify_failed, Toast.LENGTH_SHORT).show() else matches = found
        } else file?.delete()
    }

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
        onIdentify = {
            if (BuildConfig.PLANTNET_API_KEY.isBlank()) {
                Toast.makeText(context, R.string.identify_not_set_up, Toast.LENGTH_SHORT).show()
            } else {
                val file = File.createTempFile("plant", ".jpg", File(context.cacheDir, "identify").apply { mkdirs() })
                photoFile = file
                try {
                    camera.launch(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
                } catch (e: ActivityNotFoundException) {
                    file.delete()
                    Toast.makeText(context, R.string.no_camera_app, Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = modifier
    )

    if (identifying) IdentifyingDialog()
    matches?.let { found ->
        IdentifyResultsDialog(
            matches = found,
            types = viewModel.plantTypes,
            onPickType = { identifiedType = it; matches = null },
            onClose = { matches = null }
        )
    }
    identifiedType?.let { type ->
        val stand = Plant(0, type.name, type)
        SpotRankingDialog(stand, viewModel.rankSpotsFor(stand), onDismiss = { identifiedType = null })
    }

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
    onIdentify: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LargeTitleTopBar(
            title = stringResource(R.string.tab_my_plants),
            subtitle = stringResource(R.string.my_plants_subtitle),
            actions = {
                TopBarIconButton(R.drawable.ic_camera, stringResource(R.string.identify_plant), onClick = onIdentify)
            }
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
            onShare = {},
            onIdentify = {}
        )
    }
}
