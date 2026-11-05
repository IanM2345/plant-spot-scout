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

    private val snakePlant = PlantType("Snake plant", LightBand.LOW)
    private val pothos = PlantType("Pothos", LightBand.MEDIUM)
    private val monstera = PlantType("Monstera", LightBand.BRIGHT_INDIRECT)
    private val aloe = PlantType("Aloe vera", LightBand.DIRECT)
    private val peaceLily = PlantType("Peace lily", LightBand.LOW)

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
}
