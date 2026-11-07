package com.iangachoki.plantspotscout

import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.model.rankSpots
import org.junit.Assert.assertEquals
import org.junit.Test

class SpotRankingTest {

    private val low = Spot(1, "Hall", "Living room", LightBand.LOW)
    private val medium = Spot(2, "Shelf", "Living room", LightBand.MEDIUM)
    private val bright = Spot(3, "Desk", "Office", LightBand.BRIGHT_INDIRECT)
    private val direct = Spot(4, "Sill", "Kitchen", LightBand.DIRECT)

    @Test
    fun matchingBandComesFirst() {
        val ranked = rankSpots(LightBand.DIRECT, listOf(low, medium, bright, direct))
        assertEquals(listOf(direct, bright, medium, low), ranked)
    }

    @Test
    fun brighterSpotWinsATie() {
        val ranked = rankSpots(LightBand.MEDIUM, listOf(low, bright, medium))
        assertEquals(listOf(medium, bright, low), ranked)
    }
}
