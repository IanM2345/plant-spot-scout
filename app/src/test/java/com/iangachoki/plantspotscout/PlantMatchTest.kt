package com.iangachoki.plantspotscout

import com.iangachoki.plantspotscout.model.IdentifyError
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.PlantMatch
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.errorForStatus
import com.iangachoki.plantspotscout.model.isNotSure
import com.iangachoki.plantspotscout.model.matchingType
import com.iangachoki.plantspotscout.model.parseTopResults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantMatchTest {

    private fun result(name: String, score: Double, common: String = "") =
        """{"score":$score,"species":{"scientificNameWithoutAuthor":"$name","commonNames":[$common]}}"""

    @Test
    fun parse_keepsTopThreeWithFirstCommonName() {
        val json = """{"results":[${result("Monstera deliciosa", 0.9, "\"Swiss cheese plant\",\"Monster\"")},
            ${result("Aloe vera", 0.5)},${result("Pothos x", 0.3)},${result("Fourth y", 0.1)}]}"""
        val top = parseTopResults(json)
        assertEquals(3, top.size)
        assertEquals(PlantMatch("Monstera deliciosa", "Swiss cheese plant", 0.9f), top[0])
        assertNull(top[1].commonName)
    }

    @Test
    fun parse_keepsApiOrderAndToleratesMissingCommonNames() {
        val noCommon = """{"score":0.7,"species":{"scientificNameWithoutAuthor":"Ficus lyrata"}}"""
        val json = """{"results":[${result("Aloe vera", 0.9)},$noCommon,${result("Pothos x", 0.3)}]}"""
        val top = parseTopResults(json)
        assertEquals(listOf("Aloe vera", "Ficus lyrata", "Pothos x"), top.map { it.scientificName })
        assertNull(top[1].commonName)
    }

    @Test
    fun parse_emptyResultsList_isEmpty() = assertEquals(emptyList<PlantMatch>(), parseTopResults("""{"results":[]}"""))

    @Test
    fun status_mapsToTheRightError() {
        assertEquals(IdentifyError.NOT_FOUND, errorForStatus(404))
        assertEquals(IdentifyError.BAD_KEY, errorForStatus(401))
        assertEquals(IdentifyError.BAD_KEY, errorForStatus(403))
        assertEquals(IdentifyError.LIMIT, errorForStatus(429))
        assertEquals(IdentifyError.OTHER, errorForStatus(500))
    }

    @Test
    fun lowTopScore_isNotSure() {
        assertTrue(isNotSure(listOf(PlantMatch("A b", null, 0.19f), PlantMatch("C d", null, 0.1f))))
        assertFalse(isNotSure(listOf(PlantMatch("A b", null, 0.2f))))
    }

    @Test
    fun parse_noResults_isEmpty() = assertEquals(emptyList<PlantMatch>(), parseTopResults("{}"))

    @Test
    fun match_usesGenusOnly() {
        val monstera = PlantType("Monstera", LightBand.BRIGHT_INDIRECT, "Monstera deliciosa")
        val types = listOf(PlantType("Pothos", LightBand.MEDIUM), monstera)
        assertEquals(monstera, matchingType(types, PlantMatch("Monstera adansonii", null, 0.4f)))
        assertNull(matchingType(types, PlantMatch("Ficus lyrata", null, 0.4f)))
    }

    @Test
    fun match_acceptsEitherSnakePlantGenus() {
        val snake = PlantType("Snake plant", LightBand.LOW, "Dracaena trifasciata")
        assertEquals(snake, matchingType(listOf(snake), PlantMatch("Sansevieria trifasciata", null, 0.9f)))
        assertEquals(snake, matchingType(listOf(snake), PlantMatch("Dracaena trifasciata", null, 0.9f)))
    }
}
