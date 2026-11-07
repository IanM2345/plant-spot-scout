package com.iangachoki.plantspotscout.ui

import androidx.lifecycle.ViewModel
import com.iangachoki.plantspotscout.data.PlantRepository
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.Spot
import com.iangachoki.plantspotscout.model.rankSpots
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
    val demoMode: StateFlow<Boolean> = repository.demoMode

    fun setDemoMode(enabled: Boolean) {
        repository.setDemoMode(enabled)
    }

    fun clearAllData() {
        repository.clearAll()
    }

    fun addSpot(name: String, room: String, lightBand: LightBand = LightBand.MEDIUM) {
        if (name.isBlank()) return
        repository.addSpot(name, room, lightBand)
    }

    fun updateSpot(id: Long, name: String, room: String) {
        if (name.isBlank()) return
        repository.updateSpot(id, name, room)
    }

    fun updateLightBand(id: Long, lightBand: LightBand) {
        repository.updateLightBand(id, lightBand)
    }

    fun deleteSpot(id: Long) {
        repository.deleteSpot(id)
    }

    /** Spots ordered from best to worst for [plant]. */
    fun rankSpotsFor(plant: Plant): List<Spot> = rankSpots(plant.type.lightNeeds, spots.value)

    fun spotName(spotId: Long?): String? =
        spots.value.firstOrNull { it.id == spotId }?.name
}
