package com.iangachoki.plantspotscout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.iangachoki.plantspotscout.ui.spots.SpotDetailScreen
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

/**
 * Shows one spot. Start it with [newIntent] so the spot ID is always passed.
 */
class SpotDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantSpotScoutTheme {
                SpotDetailScreen(onBack = ::finish)
            }
        }
    }

    companion object {
        const val EXTRA_SPOT_ID = "com.iangachoki.plantspotscout.extra.SPOT_ID"

        fun newIntent(context: Context, spotId: Long): Intent =
            Intent(context, SpotDetailActivity::class.java).putExtra(EXTRA_SPOT_ID, spotId)
    }
}
