package com.iangachoki.plantspotscout.model

import java.net.URLEncoder

/**
 * A kind of plant and the light it needs to do well.
 */
data class PlantType(
    val name: String,
    val lightNeeds: LightBand
) {
    /** A web search for care tips. Good enough until each type has its own curated page. */
    val careUrl: String
        get() = "https://www.google.com/search?q=" + URLEncoder.encode("$name care guide", "UTF-8")
}
