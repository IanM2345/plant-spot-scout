package com.iangachoki.plantspotscout

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.spots.SpotDetailScreen
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Shows one spot. Start it with [newIntent] so the spot ID is always passed.
 */
class SpotDetailActivity : ComponentActivity() {

    private val viewModel: SpotsViewModel by viewModels()
    private var spotId = NO_SPOT_ID

    // Registered before onCreate finishes, as the Activity Result API requires.
    private val measureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == RESULT_OK && data != null && data.hasExtra(MeasureSessionActivity.EXTRA_LUX)) {
            val lux = data.getFloatExtra(MeasureSessionActivity.EXTRA_LUX, 0f)
            val band = LightBand.fromLux(lux)
            viewModel.updateLightBand(spotId, band)
            Toast.makeText(this, getString(R.string.light_band_updated, band.label), Toast.LENGTH_SHORT).show()
        }
    }

    // The camera app hands back a small preview bitmap, which is plenty for a spot card.
    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            savePhoto(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spotId = intent.getLongExtra(EXTRA_SPOT_ID, NO_SPOT_ID)
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
                    },
                    onTakePhoto = { takePhoto() }
                )
            }
        }
    }

    private fun takePhoto() {
        try {
            photoLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_camera_app, Toast.LENGTH_SHORT).show()
        }
    }

    /** Writes the photo to app storage and points the spot at it, replacing any old photo. */
    private fun savePhoto(bitmap: Bitmap) {
        val oldPhoto = viewModel.spots.value.firstOrNull { it.id == spotId }?.photoUri
        lifecycleScope.launch {
            val file = withContext(Dispatchers.IO) {
                val dir = File(filesDir, "spot_photos").apply { mkdirs() }
                val file = File(dir, "spot_${spotId}_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                oldPhoto?.let { Uri.parse(it).path }?.let { File(it).delete() }
                file
            }
            viewModel.updatePhoto(spotId, Uri.fromFile(file).toString())
        }
    }

    companion object {
        const val EXTRA_SPOT_ID = "com.iangachoki.plantspotscout.extra.SPOT_ID"
        private const val NO_SPOT_ID = -1L

        fun newIntent(context: Context, spotId: Long): Intent =
            Intent(context, SpotDetailActivity::class.java).putExtra(EXTRA_SPOT_ID, spotId)
    }
}
