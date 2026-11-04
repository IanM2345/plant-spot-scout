package com.iangachoki.plantspotscout.model

/**
 * How much light a spot gets, from darkest to brightest.
 */
enum class LightBand(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    BRIGHT_INDIRECT("Bright indirect"),
    DIRECT("Direct sun")
}
