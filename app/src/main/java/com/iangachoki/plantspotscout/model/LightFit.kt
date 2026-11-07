package com.iangachoki.plantspotscout.model

/**
 * How well a spot's light suits a plant.
 */
enum class LightFit(val label: String) {
    GOOD("Good fit"),
    TOO_DARK("Too dark"),
    TOO_BRIGHT("Too bright");

    companion object {
        fun of(needs: LightBand, spot: LightBand): LightFit = when {
            spot == needs -> GOOD
            spot < needs -> TOO_DARK
            else -> TOO_BRIGHT
        }
    }
}
