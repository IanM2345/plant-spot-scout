package com.iangachoki.plantspotscout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.iangachoki.plantspotscout.ui.measure.MeasureSessionScreen
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

/**
 * Measures the light in one spot. Spot Detail starts it for a result with [newIntent].
 */
class MeasureSessionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spotName = intent.getStringExtra(EXTRA_SPOT_NAME).orEmpty()
        enableEdgeToEdge()
        setContent {
            PlantSpotScoutTheme {
                MeasureSessionScreen(
                    spotName = spotName,
                    onCancel = ::finish
                )
            }
        }
    }

    companion object {
        const val EXTRA_SPOT_NAME = "com.iangachoki.plantspotscout.extra.SPOT_NAME"

        fun newIntent(context: Context, spotName: String): Intent =
            Intent(context, MeasureSessionActivity::class.java).putExtra(EXTRA_SPOT_NAME, spotName)
    }
}
