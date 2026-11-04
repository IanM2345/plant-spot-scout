package com.iangachoki.plantspotscout.ui

import androidx.lifecycle.ViewModel
import com.iangachoki.plantspotscout.data.PlantRepository
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.Spot
import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes spots and plants from [PlantRepository] to the UI.
 */
class SpotsViewModel(
    private val repository: PlantRepository = PlantRepository
) : ViewModel() {

    val spots: StateFlow<List<Spot>> = repository.spots
    val plants: StateFlow<List<Plant>> = repository.plants
    val rooms: List<String> = repository.rooms

    fun addSpot(name: String, room: String, lightBand: LightBand = LightBand.MEDIUM) {
        if (name.isBlank()) return
        repository.addSpot(name, room, lightBand)
    }

    fun spotName(spotId: Long?): String? =
        spots.value.firstOrNull { it.id == spotId }?.name
}
