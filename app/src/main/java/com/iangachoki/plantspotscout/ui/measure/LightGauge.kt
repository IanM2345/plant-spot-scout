package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val START_ANGLE = 150f
private const val SWEEP_ANGLE = 240f

/**
 * Arc gauge for a lux reading. Uses a square root scale so the dim bands are not squashed
 * into a sliver at the start of the arc. Ticks across the track mark where each band starts.
 */
@Composable
fun LightGauge(lux: Float?, modifier: Modifier = Modifier, size: Dp = 240.dp) {
    val target = lux?.let { gaugeFraction(it) } ?: 0f
    val fraction by animateFloatAsState(target, label = "gauge")
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val valueColor = MaterialTheme.colorScheme.primary
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(size)) {
                val strokeWidth = 20.dp.toPx()
                val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                drawArc(trackColor, START_ANGLE, SWEEP_ANGLE, useCenter = false, style = stroke)
                if (fraction > 0f) {
                    drawArc(valueColor, START_ANGLE, SWEEP_ANGLE * fraction, useCenter = false, style = stroke)
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
            }
            Text(
                text = lux?.let { stringResource(R.string.lux_value, it.toInt()) } ?: "–",
                style = MaterialTheme.typography.displaySmall
            )
        }
        // The arc's open bottom leaves room, so pull the scale labels up under its ends.
        Row(
            modifier = Modifier
                .width(size)
                .offset(y = -(size / 6)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.gauge_min),
                style = MaterialTheme.typography.labelSmall,
                color = tickColor,
                modifier = Modifier.padding(start = 28.dp)
            )
            Text(
                stringResource(R.string.gauge_max),
                style = MaterialTheme.typography.labelSmall,
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
