package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme
import kotlin.math.sqrt

private const val START_ANGLE = 150f
private const val SWEEP_ANGLE = 240f

/**
 * Arc gauge for a lux reading. Uses a square root scale so the dim bands are not squashed
 * into a sliver at the start of the arc.
 */
@Composable
fun LightGauge(lux: Float?, modifier: Modifier = Modifier, size: Dp = 240.dp) {
    val target = lux?.let { sqrt(it.coerceIn(0f, LightBand.MAX_LUX) / LightBand.MAX_LUX) } ?: 0f
    val fraction by animateFloatAsState(target, label = "gauge")
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val valueColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            drawArc(trackColor, START_ANGLE, SWEEP_ANGLE, useCenter = false, style = stroke)
            if (fraction > 0f) {
                drawArc(valueColor, START_ANGLE, SWEEP_ANGLE * fraction, useCenter = false, style = stroke)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = lux?.let { stringResource(R.string.lux_value, it.toInt()) } ?: "–",
                style = MaterialTheme.typography.displaySmall
            )
            if (lux != null) {
                Text(
                    LightBand.fromLux(lux).label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LightGaugePreview() {
    PlantSpotScoutTheme {
        LightGauge(lux = 3_200f)
    }
}
