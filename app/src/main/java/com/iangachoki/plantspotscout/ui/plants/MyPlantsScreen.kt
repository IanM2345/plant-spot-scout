package com.iangachoki.plantspotscout.ui.plants

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.ui.SpotsViewModel

@Composable
fun MyPlantsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val plants by viewModel.plants.collectAsState()
    val spots by viewModel.spots.collectAsState()

    LazyColumn(modifier = modifier) {
        items(plants, key = { it.id }) { plant ->
            val spotName = spots.firstOrNull { it.id == plant.currentSpotId }?.name
            ListItem(
                headlineContent = { Text(plant.nickname) },
                supportingContent = {
                    Text("${plant.type.name} · needs ${plant.type.lightNeeds.label} light")
                },
                trailingContent = { Text(spotName ?: stringResource(R.string.no_spot)) }
            )
        }
    }
}
