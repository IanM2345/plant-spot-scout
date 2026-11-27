package com.iangachoki.plantspotscout.data

import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.Plant
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.Spot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Keeps spots and plants in memory. Data resets when the app process dies.
 */
object PlantRepository {

    val rooms = listOf("Living room", "Bedroom", "Kitchen", "Bathroom", "Balcony", "Office")

    private val snakePlant = PlantType("Snake plant", LightBand.LOW, "Dracaena trifasciata")
    private val pothos = PlantType("Pothos", LightBand.MEDIUM, "Epipremnum aureum")
    private val monstera = PlantType("Monstera", LightBand.BRIGHT_INDIRECT, "Monstera deliciosa")
    private val aloe = PlantType("Aloe vera", LightBand.DIRECT, "Aloe vera")
    private val peaceLily = PlantType("Peace lily", LightBand.LOW, "Spathiphyllum wallisii")

    val plantTypes = listOf(snakePlant, pothos, monstera, aloe, peaceLily)

    private val _spots = MutableStateFlow(
        listOf(
            Spot(1, "Window sill", "Kitchen", LightBand.DIRECT),
            Spot(2, "Bookshelf corner", "Living room", LightBand.MEDIUM),
            Spot(3, "Next to the bed", "Bedroom", LightBand.LOW),
            Spot(4, "Desk by the window", "Office", LightBand.BRIGHT_INDIRECT),
            Spot(5, "Shower shelf", "Bathroom", LightBand.LOW)
        )
    )
    val spots: StateFlow<List<Spot>> = _spots.asStateFlow()

    private val _plants = MutableStateFlow(
        listOf(
            Plant(1, "Sammy", snakePlant, currentSpotId = 3),
            Plant(2, "Trailblazer", pothos, currentSpotId = 2),
            Plant(3, "Big Mo", monstera, currentSpotId = 4),
            Plant(4, "Spike", aloe, currentSpotId = 1),
            Plant(5, "Lily", peaceLily, currentSpotId = null)
        )
    )
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val _demoMode = MutableStateFlow(false)

    /** When on, Measure Session starts with simulated readings instead of the slider. */
    val demoMode: StateFlow<Boolean> = _demoMode.asStateFlow()

    fun setDemoMode(enabled: Boolean) {
        _demoMode.value = enabled
    }

    /** Removes every spot and plant. */
    fun clearAll() {
        _plants.value = emptyList()
        _spots.value = emptyList()
    }

    fun addSpot(name: String, room: String, lightBand: LightBand = LightBand.MEDIUM): Spot {
        val nextId = (_spots.value.maxOfOrNull { it.id } ?: 0L) + 1
        val spot = Spot(nextId, name.trim(), room, lightBand)
        _spots.update { it + spot }
        return spot
    }

    fun updateSpot(id: Long, name: String, room: String) {
        _spots.update { spots ->
            spots.map { if (it.id == id) it.copy(name = name.trim(), room = room) else it }
        }
    }

    fun updateLightBand(id: Long, lightBand: LightBand) {
        _spots.update { spots ->
            spots.map { if (it.id == id) it.copy(lightBand = lightBand) else it }
        }
    }

    fun updatePhoto(id: Long, photoUri: String) {
        _spots.update { spots ->
            spots.map { if (it.id == id) it.copy(photoUri = photoUri) else it }
        }
    }

    /** Removes the spot. Plants that were in it are left without a spot. */
    fun deleteSpot(id: Long) {
        _spots.update { spots -> spots.filterNot { it.id == id } }
        _plants.update { plants ->
            plants.map { if (it.currentSpotId == id) it.copy(currentSpotId = null) else it }
        }
    }
}
