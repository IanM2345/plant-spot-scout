package com.iangachoki.plantspotscout.ui.measure

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.averageLux
import com.iangachoki.plantspotscout.model.showNoSensorScreen
import com.iangachoki.plantspotscout.model.tooMuchMovement
import com.iangachoki.plantspotscout.ui.components.BackTopBar
import com.iangachoki.plantspotscout.ui.theme.OnWarningContainer
import com.iangachoki.plantspotscout.ui.theme.Spacing
import com.iangachoki.plantspotscout.ui.theme.WarningContainer
import kotlinx.coroutines.delay
import kotlin.math.sqrt
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
    demoModeOn: Boolean,
    onTurnOnDemo: () -> Unit,
    onCancel: () -> Unit,
    onUseReading: (lux: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by rememberSaveable { mutableStateOf(Phase.READY) }
    var secondsLeft by rememberSaveable { mutableIntStateOf(SESSION_SECONDS) }
    var useRandom by rememberSaveable { mutableStateOf(startWithRandom) }
    var demoOn by rememberSaveable { mutableStateOf(demoModeOn) }
    var sliderLux by rememberSaveable { mutableFloatStateOf(1_000f) }
    val readings = remember { mutableStateListOf<Float>() }
    // Saved separately so the final value survives rotation.
    var average by rememberSaveable { mutableStateOf<Float?>(null) }
    var tooMuchMovement by rememberSaveable { mutableStateOf(false) }
    val currentSliderLux by rememberUpdatedState(sliderLux)
    val currentUseRandom by rememberUpdatedState(useRandom)
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    var skipped by remember { mutableIntStateOf(0) }
    var moving by remember { mutableStateOf(false) }
    var lastMoveMs by remember { mutableLongStateOf(0L) }
    var lastMagnitude by remember { mutableStateOf<Float?>(null) }
    SensorEffect(sensor, phase == Phase.MEASURING) {
        // Without an accelerometer lastMoveMs stays 0, so every sample is kept.
        if (SystemClock.elapsedRealtime() - lastMoveMs < MOVE_HOLD_MS) skipped++ else readings += it[0]
    }
    SensorEffect(accelerometer, phase == Phase.MEASURING && sensor != null) {
        val magnitude = sqrt(it[0] * it[0] + it[1] * it[1] + it[2] * it[2])
        val now = SystemClock.elapsedRealtime()
        if (isMoving(magnitude, lastMagnitude)) lastMoveMs = now
        lastMagnitude = magnitude
        moving = now - lastMoveMs < MOVE_HOLD_MS
    }

    // Runs the countdown; without a light sensor it also collects one fake reading every half second.
    LaunchedEffect(phase) {
        if (phase != Phase.MEASURING) return@LaunchedEffect
        readings.clear()
        skipped = 0
        moving = false
        lastMoveMs = 0L
        lastMagnitude = null
        average = null
        tooMuchMovement = false
        val randomBase = Random.nextFloat() * 15_000f
        for (second in SESSION_SECONDS downTo 1) {
            secondsLeft = second
            repeat(READINGS_PER_SECOND) {
                delay(1_000L / READINGS_PER_SECOND)
                if (sensor == null) {
                    val base = if (currentUseRandom) randomBase else currentSliderLux
                    readings += simulatedReading(base)
                }
            }
        }
        secondsLeft = 0
        tooMuchMovement = tooMuchMovement(readings.size, skipped)
        average = if (tooMuchMovement) null else averageLux(readings)
        phase = Phase.DONE
    }

    if (showNoSensorScreen(sensor != null, demoOn)) {
        NoLightSensor(
            spotName = spotName,
            onCancel = onCancel,
            onTurnOnDemo = {
                onTurnOnDemo()
                demoOn = true
                useRandom = true
                phase = Phase.MEASURING
            },
            onPickBand = { onUseReading(it.midLux) },
            modifier = modifier
        )
        return
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
                Button(onClick = { average?.let(onUseReading) }, enabled = phase == Phase.DONE && average != null) {
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
            if (moving && phase == Phase.MEASURING) {
                Surface(
                    color = WarningContainer,
                    contentColor = OnWarningContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_phone), contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.hold_still), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (tooMuchMovement && phase == Phase.DONE) {
                Text(
                    stringResource(R.string.too_much_movement),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

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
                Text(
                    stringResource(R.string.samples_kept, readings.size, skipped),
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

            if (sensor == null) {
                Text(
                    stringResource(R.string.no_light_sensor),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (sensor == null && phase != Phase.DONE) {
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
