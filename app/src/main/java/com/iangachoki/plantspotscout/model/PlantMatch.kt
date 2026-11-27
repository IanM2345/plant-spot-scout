package com.iangachoki.plantspotscout.model

import org.json.JSONObject

/** One Pl@ntNet suggestion. [score] is 0 to 1. */
data class PlantMatch(val scientificName: String, val commonName: String?, val score: Float)

/** The best three results of a Pl@ntNet identify response, in the order the API sent them. */
fun parseTopResults(json: String): List<PlantMatch> {
    val results = JSONObject(json).optJSONArray("results") ?: return emptyList()
    return (0 until minOf(3, results.length())).map { i ->
        val result = results.getJSONObject(i)
        val species = result.getJSONObject("species")
        PlantMatch(
            scientificName = species.getString("scientificNameWithoutAuthor"),
            commonName = species.optJSONArray("commonNames")?.optString(0)?.takeIf { it.isNotBlank() },
            score = result.getDouble("score").toFloat()
        )
    }
}

// Sansevieria was folded into Dracaena, and Pl@ntNet may return either name.
private fun genus(scientificName: String) =
    scientificName.substringBefore(' ').lowercase().let { if (it == "sansevieria") "dracaena" else it }

/** The type with the same genus (first word of the scientific name) as [match], if we have one. */
fun matchingType(types: List<PlantType>, match: PlantMatch): PlantType? {
    val target = genus(match.scientificName)
    return types.firstOrNull { it.scientificName.isNotBlank() && genus(it.scientificName) == target }
}
