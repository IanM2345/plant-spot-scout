package com.iangachoki.plantspotscout.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
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
    onCancel: () -> Unit,
    onUseReading: (lux: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by rememberSaveable { mutableStateOf(Phase.READY) }
    var secondsLeft by rememberSaveable { mutableIntStateOf(SESSION_SECONDS) }
    var useRandom by rememberSaveable { mutableStateOf(true) }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LightGauge(lux = if (phase == Phase.DONE) average else latest)

            when (phase) {
                Phase.READY -> Text(
                    stringResource(R.string.measure_hint),
                    style = MaterialTheme.typography.bodyLarge
                )
                Phase.MEASURING -> {
                    Text(
                        stringResource(R.string.seconds_left, secondsLeft),
                        style = MaterialTheme.typography.titleMedium
                    )
                    LinearProgressIndicator(
                        progress = { 1f - secondsLeft / SESSION_SECONDS.toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Phase.DONE -> Text(
                    stringResource(R.string.average_reading, SESSION_SECONDS * READINGS_PER_SECOND),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.random_readings), modifier = Modifier.weight(1f))
                Switch(checked = useRandom, onCheckedChange = { useRandom = it })
            }
            if (!useRandom) {
                Text(stringResource(R.string.slider_lux, sliderLux.toInt()))
                Slider(
                    value = sliderLux,
                    onValueChange = { sliderLux = it },
                    valueRange = 0f..LightBand.MAX_LUX,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val result = average
            if (phase == Phase.DONE && result != null) {
                Button(onClick = { onUseReading(result) }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.use_reading))
                }
                OutlinedButton(onClick = { phase = Phase.MEASURING }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.measure_again))
                }
            } else {
                Button(
                    onClick = { phase = Phase.MEASURING },
                    enabled = phase != Phase.MEASURING,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.start_measuring))
                }
            }
        }
    }
}

/** A fake sensor value: the base with about 10% noise either way. */
private fun simulatedReading(base: Float): Float {
    val noise = 1f + (Random.nextFloat() - 0.5f) * 0.2f
    return (base * noise).coerceIn(0f, LightBand.MAX_LUX)
}
