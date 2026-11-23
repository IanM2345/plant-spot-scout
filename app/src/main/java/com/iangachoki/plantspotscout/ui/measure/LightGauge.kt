package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme
import com.iangachoki.plantspotscout.ui.theme.containerColor
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val START_ANGLE = 180f
private const val SWEEP_ANGLE = 180f

/**
 * Arc gauge for a lux reading. Uses a square root scale so the dim bands are not squashed
 * into a sliver at the start of the arc. Ticks across the track mark where each band starts.
 */
@Composable
fun LightGauge(lux: Float?, modifier: Modifier = Modifier, size: Dp = 240.dp) {
    val target = lux?.let { gaugeFraction(it) } ?: 0f
    val fraction by animateFloatAsState(target, label = "gauge")
    val needleColor = MaterialTheme.colorScheme.onSurface
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // Only the top half of the square canvas holds the arc.
        Box(modifier = Modifier.size(size, size / 2 + 4.dp), contentAlignment = Alignment.TopCenter) {
            Canvas(modifier = Modifier.requiredSize(size)) {
                val strokeWidth = 20.dp.toPx()
                val stroke = Stroke(width = strokeWidth)
                LightBand.entries.forEachIndexed { i, band ->
                    val from = gaugeFraction(band.minLux)
                    val to = LightBand.entries.getOrNull(i + 1)?.let { gaugeFraction(it.minLux) } ?: 1f
                    drawArc(band.containerColor(), START_ANGLE + SWEEP_ANGLE * from, SWEEP_ANGLE * (to - from), useCenter = false, style = stroke)
                }
                val radius = this.size.minDimension / 2f
                val inner = radius - strokeWidth * 0.75f
                val outer = radius + strokeWidth * 0.75f
                LightBand.entries.drop(1).forEach { band ->
                    val angle = Math.toRadians((START_ANGLE + SWEEP_ANGLE * gaugeFraction(band.minLux)).toDouble())
                    val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                    drawLine(
                        color = tickColor,
                        start = center + direction * inner,
                        end = center + direction * outer,
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                if (fraction > 0f) {
                    val angle = Math.toRadians((START_ANGLE + SWEEP_ANGLE * fraction).toDouble())
                    val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                    drawLine(needleColor, center + direction * inner, center + direction * outer, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                }
            }
        }
        Row(
            modifier = Modifier.width(size),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.gauge_min),
                style = MaterialTheme.typography.labelMedium,
                color = tickColor
            )
            Text(
                stringResource(R.string.gauge_max),
                style = MaterialTheme.typography.labelMedium,
                color = tickColor
            )
        }
    }
}

/** Where [lux] sits along the arc, from 0 to 1. */
private fun gaugeFraction(lux: Float): Float = sqrt(lux.coerceIn(0f, LightBand.MAX_LUX) / LightBand.MAX_LUX)

@Preview(showBackground = true)
@Composable
private fun LightGaugePreview() {
    PlantSpotScoutTheme {
        LightGauge(lux = 3_200f)
    }
}
