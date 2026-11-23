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

/**
 * Feeds ambient light readings (lux) to [onLux] while [active] and the screen is resumed.
 * The listener is registered only then and unregistered on end, leave or pause: the
 * sensor keeps the hardware awake, so listening in the background drains the battery.
 */
@Composable
fun LightSensorEffect(sensor: Sensor?, active: Boolean, onLux: (Float) -> Unit) {
    val manager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLux by rememberUpdatedState(onLux)
    DisposableEffect(sensor, active, lifecycle) {
        if (sensor == null || !active) return@DisposableEffect onDispose {}
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) = currentOnLux(event.values[0])
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
