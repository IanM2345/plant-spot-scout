package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.ui.SpotsViewModel

@Composable
fun MySpotsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.tab_my_spots))
    }
}
