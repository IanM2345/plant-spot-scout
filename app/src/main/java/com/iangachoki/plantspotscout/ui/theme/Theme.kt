package com.iangachoki.plantspotscout.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Light scheme built only from Color.kt. Slots the design does not name reuse the closest design colour,
// so no Material baseline (purple) colours leak through.
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = PrimaryContainer,
    secondary = Primary,
    onSecondary = OnPrimary,
    secondaryContainer = PrimaryContainer,
    onSecondaryContainer = OnPrimaryContainer,
    tertiary = Primary,
    onTertiary = OnPrimary,
    tertiaryContainer = PrimaryContainer,
    onTertiaryContainer = OnPrimaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = Primary,
    inverseSurface = OnSurface,
    inverseOnSurface = Surface,
    outline = Outline,
    outlineVariant = OutlineVariant,
    surfaceBright = Surface,
    surfaceDim = SurfaceVariant,
    surfaceContainerLowest = Surface,
    surfaceContainerLow = Surface,
    surfaceContainer = Surface,
    surfaceContainerHigh = SurfaceVariant,
    surfaceContainerHighest = SurfaceVariant,
    error = Error,
    onError = OnError,
    errorContainer = Error,
    onErrorContainer = OnError
)

// Corner radii: small for chips, medium for cards. Buttons and band chips use CircleShape (pill) directly.
private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun PlantSpotScoutTheme(
    // The design is light only for now, so this is ignored. Kept so callers do not change.
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamic colour is off on purpose: the app always uses its own palette.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
