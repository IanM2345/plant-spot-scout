package com.iangachoki.plantspotscout.model

import androidx.annotation.StringRes
import com.iangachoki.plantspotscout.R
import kotlin.math.abs

/** How many bands apart a plant's preferred band and a spot's band are. 0 is a good fit. */
fun bandDistance(needs: LightBand, spot: LightBand): Int = abs(spot.ordinal - needs.ordinal)

/** String resource for the short reason a spot suits (or not) a plant. Takes the plant type name as %1$s. */
@StringRes
fun spotReason(needs: LightBand, spot: LightBand): Int = when {
    LightFit.of(needs, spot) == LightFit.GOOD -> R.string.reason_best
    spot > needs -> R.string.reason_bright
    bandDistance(needs, spot) == 1 -> R.string.reason_dim
    else -> R.string.reason_dark
}

/**
 * Orders spots from best to worst for a plant that needs [needs] light.
 *
 * Simple for now: spots in the right band come first, then the ones one band away, and so on.
 * When two spots are equally far off, the brighter one wins, since most houseplants cope
 * with a bit too much light better than too little.
 */
fun rankSpots(needs: LightBand, spots: List<Spot>): List<Spot> =
    spots.sortedWith(
        compareBy<Spot> { bandDistance(needs, it.lightBand) }
            .thenByDescending { it.lightBand.ordinal }
            .thenBy { it.name }
    )
