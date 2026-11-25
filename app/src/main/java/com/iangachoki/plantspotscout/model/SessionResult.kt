package com.iangachoki.plantspotscout.model

/** A session needs at least this share of all samples kept, or the phone moved too much. */
const val MIN_KEPT_FRACTION = 0.5f

/** Mean of the kept samples, or null when there are none. */
fun averageLux(samples: List<Float>): Float? = if (samples.isEmpty()) null else samples.average().toFloat()

/** True when too few samples were kept (kept < skipped at the default 0.5). */
fun tooMuchMovement(kept: Int, skipped: Int): Boolean = kept < MIN_KEPT_FRACTION * (kept + skipped)
