package com.iangachoki.plantspotscout.ui.measure

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.math.abs

/** Phone counts as moving when acceleration is this far (m/s^2) from gravity, or from the previous event. */
const val MOVE_THRESHOLD = 0.6f

/** How long (ms) the phone stays "moving" after the last movement, so the banner does not flicker. */
const val MOVE_HOLD_MS = 500L

private const val GRAVITY = 9.81f

/** [magnitude] is the acceleration length in m/s^2; [previous] is the last event's, or null for the first. */
fun isMoving(magnitude: Float, previous: Float?): Boolean =
    abs(magnitude - GRAVITY) > MOVE_THRESHOLD || (previous != null && abs(magnitude - previous) > MOVE_THRESHOLD)

/**
 * Feeds [sensor] values to [onValues] while [active] and the screen is resumed.
 * The listener is registered only then and unregistered on end, leave or pause: the
 * sensor keeps the hardware awake, so listening in the background drains the battery.
 */
@Composable
fun SensorEffect(sensor: Sensor?, active: Boolean, onValues: (FloatArray) -> Unit) {
    val manager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnValues by rememberUpdatedState(onValues)
    DisposableEffect(sensor, active, lifecycle) {
        if (sensor == null || !active) return@DisposableEffect onDispose {}
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) = currentOnValues(event.values)
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME ->
                    manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                Lifecycle.Event.ON_PAUSE -> manager.unregisterListener(listener)
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            manager.unregisterListener(listener)
        }
    }
}
