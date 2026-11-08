package com.iangachoki.plantspotscout.model

import kotlin.math.abs

/**
 * Orders spots from best to worst for a plant that needs [needs] light.
 *
 * Simple for now: spots in the right band come first, then the ones one band away, and so on.
 * When two spots are equally far off, the brighter one wins, since most houseplants cope
 * with a bit too much light better than too little.
 */
fun rankSpots(needs: LightBand, spots: List<Spot>): List<Spot> =
    spots.sortedWith(
        compareBy<Spot> { abs(it.lightBand.ordinal - needs.ordinal) }
            .thenByDescending { it.lightBand.ordinal }
            .thenBy { it.name }
    )
