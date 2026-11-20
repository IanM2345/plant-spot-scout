package com.iangachoki.plantspotscout.ui.theme

import androidx.compose.ui.graphics.Color
import com.iangachoki.plantspotscout.model.LightBand

// "Cyanotype" palette from the Figma design. This is the only file that holds colour values.

// Brand blue: buttons, selected nav item, links.
val Primary = Color(0xFF1E4E8C)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD8E4F5)
val OnPrimaryContainer = Color(0xFF0E1A2B)

// Screen background and card surface, with the text colour that sits on them.
val Background = Color(0xFFF3F5F8)
val OnBackground = Color(0xFF0E1A2B)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF0E1A2B)

// Quieter surfaces (input fields, chips) and secondary text.
val SurfaceVariant = Color(0xFFE6ECF4)
val OnSurfaceVariant = Color(0xFF4A5668)

// Light tint used by the bottom navigation bar and menus.
val SurfaceContainer = Color(0xFFEAEFF5)

// Borders and dividers.
val Outline = Color(0xFFC9D3E0)
val OutlineVariant = Color(0xFFE1E7EF)

// Errors, e.g. "Too dark" and delete.
val Error = Color(0xFFB3261E)
val OnError = Color(0xFFFFFFFF)

// Amber banner for the "Hold still" message while measuring. Material has no warning slot, so it lives here.
val WarningContainer = Color(0xFFFBE3B0)
val OnWarningContainer = Color(0xFF0E1A2B)

/**
 * Colours for each light band: a container colour and the text colour that stays readable on it.
 * Dark to bright, so a brighter spot looks brighter.
 */
object LightBandColors {
    val Low = Color(0xFF14294A)
    val OnLow = Color(0xFFFFFFFF)

    val Medium = Color(0xFF2E5E9E)
    val OnMedium = Color(0xFFFFFFFF)

    val BrightIndirect = Color(0xFF8FB2DD)
    val OnBrightIndirect = Color(0xFF0E1A2B)

    val DirectSun = Color(0xFFF4B740)
    val OnDirectSun = Color(0xFF0E1A2B)
}

/** Fill colour for this band's chip and dot. */
fun LightBand.containerColor(): Color = when (this) {
    LightBand.LOW -> LightBandColors.Low
    LightBand.MEDIUM -> LightBandColors.Medium
    LightBand.BRIGHT_INDIRECT -> LightBandColors.BrightIndirect
    LightBand.DIRECT -> LightBandColors.DirectSun
}

/** Text colour to use on top of [containerColor]. */
fun LightBand.contentColor(): Color = when (this) {
    LightBand.LOW -> LightBandColors.OnLow
    LightBand.MEDIUM -> LightBandColors.OnMedium
    LightBand.BRIGHT_INDIRECT -> LightBandColors.OnBrightIndirect
    LightBand.DIRECT -> LightBandColors.OnDirectSun
}
