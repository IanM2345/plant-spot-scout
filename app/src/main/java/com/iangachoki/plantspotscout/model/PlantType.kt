package com.iangachoki.plantspotscout.model

/**
 * A kind of plant and the light it needs to do well.
 */
data class PlantType(
    val name: String,
    val lightNeeds: LightBand
)
