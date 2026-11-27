package com.iangachoki.plantspotscout.model

import androidx.annotation.StringRes
import com.iangachoki.plantspotscout.R

/** Below this top score the results are shown with a "not sure" note. */
private const val MIN_SURE_SCORE = 0.2f

/** Why an identification failed, with the message to show for it. */
enum class IdentifyError(@param:StringRes val message: Int) {
    OFFLINE(R.string.error_offline),
    NOT_FOUND(R.string.error_not_found),
    BAD_KEY(R.string.error_bad_key),
    LIMIT(R.string.error_limit),
    OTHER(R.string.identify_failed)
}

sealed interface Identification {
    data class Found(val matches: List<PlantMatch>) : Identification
    data class Failed(val error: IdentifyError) : Identification
}

/** Maps a non-success HTTP status from Pl@ntNet to what went wrong. */
fun errorForStatus(code: Int): IdentifyError = when (code) {
    404 -> IdentifyError.NOT_FOUND
    401, 403 -> IdentifyError.BAD_KEY
    429 -> IdentifyError.LIMIT
    else -> IdentifyError.OTHER
}

/** True when even the best match is weak. */
fun isNotSure(matches: List<PlantMatch>): Boolean = (matches.maxOfOrNull { it.score } ?: 0f) < MIN_SURE_SCORE
