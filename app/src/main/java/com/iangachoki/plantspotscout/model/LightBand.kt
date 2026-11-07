package com.iangachoki.plantspotscout.model

/**
 * How much light a spot gets, from darkest to brightest.
 */
enum class LightBand(val label: String, val minLux: Float) {
    LOW("Low", 0f),
    MEDIUM("Medium", 500f),
    BRIGHT_INDIRECT("Bright indirect", 2_500f),
    DIRECT("Direct sun", 10_000f);

    companion object {
        /** Brightest reading the gauge shows. */
        const val MAX_LUX = 20_000f

        fun fromLux(lux: Float): LightBand = entries.last { lux >= it.minLux }
    }
}
