package com.iangachoki.plantspotscout.model

// Lower edge of each band in lux. Rule-of-thumb houseplant bands, not from a cited source.
// PlantType.lightNeeds uses these same bands, so a plant's needs and a reading always compare.
private const val MEDIUM_MIN_LUX = 500f
private const val BRIGHT_INDIRECT_MIN_LUX = 2_500f
private const val DIRECT_MIN_LUX = 10_000f

/**
 * How much light a spot gets, from darkest to brightest.
 */
enum class LightBand(val label: String, val minLux: Float) {
    LOW("Low", 0f),
    MEDIUM("Medium", MEDIUM_MIN_LUX),
    BRIGHT_INDIRECT("Bright indirect", BRIGHT_INDIRECT_MIN_LUX),
    DIRECT("Direct sun", DIRECT_MIN_LUX);

    /** Lux halfway through the band; the top band ends at [MAX_LUX]. */
    val midLux: Float get() = (minLux + (entries.getOrNull(ordinal + 1)?.minLux ?: MAX_LUX)) / 2

    companion object {
        /** Brightest reading the gauge shows. */
        const val MAX_LUX = 20_000f

        fun fromLux(lux: Float): LightBand = entries.last { lux >= it.minLux }
    }
}
