package com.iangachoki.plantspotscout.model

/**
 * A plant the user owns.
 *
 * @param currentSpotId id of the [Spot] the plant is in, or null if it has no spot yet.
 */
data class Plant(
    val id: Long,
    val nickname: String,
    val type: PlantType,
    val currentSpotId: Long? = null
)
