package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.ui.components.LightBandChip
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
            TopAppBar(
                title = { Text(stringResource(R.string.measure_title, spotName)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (phase == Phase.READY) {
                Text(
                    stringResource(R.string.measure_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            val shown = if (phase == Phase.DONE) average else latest
            LightGauge(lux = shown)

            when (phase) {
                Phase.READY -> Unit
                Phase.MEASURING -> {
                    Text(
                        stringResource(R.string.seconds_left, secondsLeft),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    LinearProgressIndicator(
                        progress = { 1f - secondsLeft / SESSION_SECONDS.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Phase.DONE -> {
                    Text(
                        stringResource(R.string.average_reading, SESSION_SECONDS * READINGS_PER_SECOND),
                        style = MaterialTheme.typography.titleMedium
                    )
                    shown?.let { LightBandChip(LightBand.fromLux(it)) }
                }
            }

            if (phase != Phase.DONE) {
                DemoReadingsCard(
                    useRandom = useRandom,
                    onUseRandomChange = { useRandom = it },
                    sliderLux = sliderLux,
                    onSliderChange = { sliderLux = it }
                )
            }

            val result = average
            if (phase == Phase.DONE && result != null) {
                OutlinedButton(
                    onClick = { phase = Phase.MEASURING },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(R.string.measure_again))
                }
                Button(
                    onClick = { onUseReading(result) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(R.string.use_reading), style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Button(
                    onClick = { phase = Phase.MEASURING },
                    enabled = phase != Phase.MEASURING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(R.string.start_measuring), style = MaterialTheme.typography.titleMedium)
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
