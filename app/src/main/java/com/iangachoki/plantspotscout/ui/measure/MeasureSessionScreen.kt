package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.components.BackTopBar
import com.iangachoki.plantspotscout.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlin.random.Random

/** How long one session runs. */
private const val SESSION_SECONDS = 10

/** Readings taken per second while the session runs. */
private const val READINGS_PER_SECOND = 2

private enum class Phase { READY, MEASURING, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureSessionScreen(
    spotName: String,
    startWithRandom: Boolean,
    onCancel: () -> Unit,
    onUseReading: (lux: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by rememberSaveable { mutableStateOf(Phase.READY) }
    var secondsLeft by rememberSaveable { mutableIntStateOf(SESSION_SECONDS) }
    var useRandom by rememberSaveable { mutableStateOf(startWithRandom) }
    var sliderLux by rememberSaveable { mutableFloatStateOf(1_000f) }
    val readings = remember { mutableStateListOf<Float>() }
    // Saved separately so the final value survives rotation.
    var average by rememberSaveable { mutableStateOf<Float?>(null) }
    val currentSliderLux by rememberUpdatedState(sliderLux)
    val currentUseRandom by rememberUpdatedState(useRandom)

    // Runs the countdown and collects one fake reading every half second.
    LaunchedEffect(phase) {
        if (phase != Phase.MEASURING) return@LaunchedEffect
        readings.clear()
        average = null
        val randomBase = Random.nextFloat() * 15_000f
        for (second in SESSION_SECONDS downTo 1) {
            secondsLeft = second
            repeat(READINGS_PER_SECOND) {
                delay(1_000L / READINGS_PER_SECOND)
                val base = if (currentUseRandom) randomBase else currentSliderLux
                readings += simulatedReading(base)
            }
        }
        secondsLeft = 0
        average = readings.average().toFloat()
        phase = Phase.DONE
    }

    val latest = readings.lastOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BackTopBar(
                title = spotName,
                subtitle = stringResource(R.string.measuring_subtitle),
                onBack = onCancel
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
                Button(onClick = { average?.let(onUseReading) }, enabled = phase == Phase.DONE) {
                    Text(stringResource(R.string.use_reading))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            val shown = if (phase == Phase.DONE) average else latest
            LightGauge(lux = shown)

            shown?.let {
                Text(LightBand.fromLux(it).label, style = MaterialTheme.typography.headlineMedium)
                Text(
                    stringResource(R.string.about_lux, it.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.seconds_left, secondsLeft / 60, secondsLeft % 60),
                    style = MaterialTheme.typography.bodyMedium
                )
                // Skipped stays 0 until the accelerometer is added.
                Text(
                    stringResource(R.string.samples_kept, readings.size, 0),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LinearProgressIndicator(
                progress = { 1f - secondsLeft / SESSION_SECONDS.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                stringResource(R.string.measure_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (phase != Phase.DONE) {
                DemoReadingsCard(
                    useRandom = useRandom,
                    onUseRandomChange = { useRandom = it },
                    sliderLux = sliderLux,
                    onSliderChange = { sliderLux = it }
                )
            }

            if (phase != Phase.MEASURING) {
                OutlinedButton(onClick = { phase = Phase.MEASURING }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(if (phase == Phase.DONE) R.string.measure_again else R.string.start_measuring))
                }
            }
        }
    }
}

/** Lets the user pick random readings or set the light level with a slider. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoReadingsCard(
    useRandom: Boolean,
    onUseRandomChange: (Boolean) -> Unit,
    sliderLux: Float,
    onSliderChange: (Float) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.demo_readings),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val options = listOf(R.string.random_readings to true, R.string.slider to false)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, (label, random) ->
                    SegmentedButton(
                        selected = useRandom == random,
                        onClick = { onUseRandomChange(random) },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size)
                    ) {
                        Text(stringResource(label))
                    }
                }
            }
            if (!useRandom) {
                Text(
                    stringResource(R.string.slider_lux, sliderLux.toInt()),
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = sliderLux,
                    onValueChange = onSliderChange,
                    valueRange = 0f..LightBand.MAX_LUX,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** A fake sensor value: the base with about 10% noise either way. */
private fun simulatedReading(base: Float): Float {
    val noise = 1f + (Random.nextFloat() - 0.5f) * 0.2f
    return (base * noise).coerceIn(0f, LightBand.MAX_LUX)
}
