package com.iangachoki.plantspotscout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.spots.SpotDetailScreen
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

/**
 * Shows one spot. Start it with [newIntent] so the spot ID is always passed.
 */
class SpotDetailActivity : ComponentActivity() {

    private val viewModel: SpotsViewModel by viewModels()

    // Registered before onCreate finishes, as the Activity Result API requires.
    private val measureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // The reading is handled once Measure Session returns one.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spotId = intent.getLongExtra(EXTRA_SPOT_ID, NO_SPOT_ID)
        if (spotId == NO_SPOT_ID) {
            // Started without a spot, so there is nothing to show.
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            PlantSpotScoutTheme {
                SpotDetailScreen(
                    spotId = spotId,
                    viewModel = viewModel,
                    onBack = ::finish,
                    onDeleted = ::finish,
                    onMeasure = { spot ->
                        measureLauncher.launch(MeasureSessionActivity.newIntent(this, spot.name))
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_SPOT_ID = "com.iangachoki.plantspotscout.extra.SPOT_ID"
        private const val NO_SPOT_ID = -1L

        fun newIntent(context: Context, spotId: Long): Intent =
            Intent(context, SpotDetailActivity::class.java).putExtra(EXTRA_SPOT_ID, spotId)
    }
}
