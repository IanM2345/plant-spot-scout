package com.iangachoki.plantspotscout

import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.averageLux
import com.iangachoki.plantspotscout.model.tooMuchMovement
import com.iangachoki.plantspotscout.ui.measure.MOVE_THRESHOLD
import com.iangachoki.plantspotscout.ui.measure.isMoving
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SensorLogicTest {

    private val gravity = 9.81f

    // LightBand thresholds are private, so read them back through minLux.
    @Test
    fun valueInsideEachBand_givesThatBand() {
        val bands = LightBand.entries
        bands.forEachIndexed { i, band ->
            val top = bands.getOrNull(i + 1)?.minLux ?: LightBand.MAX_LUX
            assertEquals(band, LightBand.fromLux((band.minLux + top) / 2))
        }
    }

    @Test
    fun thresholdValue_givesTheBandAbove() {
        LightBand.entries.forEach { assertEquals(it, LightBand.fromLux(it.minLux)) }
    }

    @Test
    fun justBelowAThreshold_givesTheBandBelow() {
        LightBand.entries.zipWithNext().forEach { (below, above) ->
            assertEquals(below, LightBand.fromLux(Math.nextDown(above.minLux)))
        }
    }

    @Test
    fun zeroLux_isLow() = assertEquals(LightBand.LOW, LightBand.fromLux(0f))

    @Test
    fun average_ofEmpty_isNull() = assertNull(averageLux(emptyList()))

    @Test
    fun average_ofOne_isThatValue() = assertEquals(750f, averageLux(listOf(750f))!!, 0.001f)

    @Test
    fun average_ofSeveral_isTheMean() = assertEquals(400f, averageLux(listOf(100f, 400f, 700f))!!, 0.001f)

    @Test
    fun stillPhone_isNotMoving() = assertFalse(isMoving(gravity, previous = null))

    @Test
    fun jumpBiggerThanThreshold_isMoving() = assertTrue(isMoving(gravity + MOVE_THRESHOLD + 0.1f, previous = gravity))

    @Test
    fun changeJustUnderThreshold_isNotMoving() =
        assertFalse(isMoving(gravity + MOVE_THRESHOLD - 0.1f, previous = gravity))

    @Test
    fun moreKeptThanSkipped_passes() = assertFalse(tooMuchMovement(kept = 12, skipped = 8))

    @Test
    fun moreSkippedThanKept_fails() = assertTrue(tooMuchMovement(kept = 8, skipped = 12))
}
