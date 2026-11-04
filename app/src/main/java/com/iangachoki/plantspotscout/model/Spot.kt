package com.iangachoki.plantspotscout.model

/**
 * A place in the home where a plant could live.
 *
 * @param photoUri optional photo of the spot; null until the user takes one.
 */
data class Spot(
    val id: Long,
    val name: String,
    val room: String,
    val lightBand: LightBand,
    val photoUri: String? = null
)
