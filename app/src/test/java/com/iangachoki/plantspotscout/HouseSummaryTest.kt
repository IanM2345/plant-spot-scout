package com.iangachoki.plantspotscout

import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.ui.spots.countSpotsByBand
import org.junit.Assert.assertEquals
import org.junit.Test

class HouseSummaryTest {

    private fun spot(id: Long, band: LightBand) = Spot(id, "Spot $id", "Room", band)

    @Test
    fun emptyList_hasNoBands() {
        assertEquals(emptyMap<LightBand, Int>(), countSpotsByBand(emptyList()))
    }

    @Test
    fun onePerBand_countsEachOnce() {
        val spots = LightBand.entries.mapIndexed { i, band -> spot(i.toLong(), band) }
        val counts = countSpotsByBand(spots)
        assertEquals(LightBand.entries, counts.keys.toList())
        assertEquals(setOf(1), counts.values.toSet())
    }

    @Test
    fun severalInOneBand_areSummed() {
        val spots = listOf(spot(1, LightBand.MEDIUM), spot(2, LightBand.MEDIUM), spot(3, LightBand.MEDIUM))
        assertEquals(mapOf(LightBand.MEDIUM to 3), countSpotsByBand(spots))
    }
}
