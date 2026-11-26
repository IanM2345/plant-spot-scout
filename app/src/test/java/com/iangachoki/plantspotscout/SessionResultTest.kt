package com.iangachoki.plantspotscout

import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.averageLux
import com.iangachoki.plantspotscout.model.showNoSensorScreen
import com.iangachoki.plantspotscout.model.tooMuchMovement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionResultTest {

    @Test
    fun average_ofEmpty_isNull() = assertNull(averageLux(emptyList()))

    @Test
    fun average_isTheMean() = assertEquals(200f, averageLux(listOf(100f, 300f))!!, 0.001f)

    @Test
    fun movement_fewerKeptThanSkipped_isTooMuch() = assertTrue(tooMuchMovement(kept = 3, skipped = 4))

    @Test
    fun movement_exactlyHalf_isFine() = assertFalse(tooMuchMovement(kept = 4, skipped = 4))

    @Test
    fun movement_noSkips_isFine() = assertFalse(tooMuchMovement(kept = 20, skipped = 0))

    @Test
    fun noSensorScreen_showsOnlyWithoutSensorAndWithoutDemo() {
        assertTrue(showNoSensorScreen(hasLightSensor = false, demoModeOn = false))
        assertFalse(showNoSensorScreen(hasLightSensor = false, demoModeOn = true))
        assertFalse(showNoSensorScreen(hasLightSensor = true, demoModeOn = false))
        assertFalse(showNoSensorScreen(hasLightSensor = true, demoModeOn = true))
    }

    @Test
    fun bandMidpoint_staysInItsBand() {
        LightBand.entries.forEach { assertEquals(it, LightBand.fromLux(it.midLux)) }
    }
}
