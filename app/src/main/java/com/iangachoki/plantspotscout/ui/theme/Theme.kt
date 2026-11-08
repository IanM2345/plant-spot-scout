package com.iangachoki.plantspotscout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Walnut,
    onPrimary = Cream,
    primaryContainer = Stone,
    onPrimaryContainer = Espresso,
    inversePrimary = Taupe,
    secondary = Taupe,
    onSecondary = Espresso,
    secondaryContainer = Linen,
    onSecondaryContainer = Espresso,
    tertiary = Espresso,
    onTertiary = Linen,
    tertiaryContainer = Linen,
    onTertiaryContainer = Espresso,
    background = Cream,
    onBackground = Espresso,
    surface = Cream,
    onSurface = Espresso,
    surfaceVariant = Linen,
    onSurfaceVariant = Walnut,
    surfaceTint = Walnut,
    inverseSurface = Espresso,
    inverseOnSurface = Linen,
    outline = Taupe,
    outlineVariant = Stone,
    surfaceBright = Cream,
    surfaceDim = Linen,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Cream,
    surfaceContainer = Parchment,
    surfaceContainerHigh = Parchment,
    surfaceContainerHighest = Linen,
    error = Rust,
    onError = Cream,
    errorContainer = RustContainer,
    onErrorContainer = OnRustContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Taupe,
    onPrimary = Espresso,
    primaryContainer = Walnut,
    onPrimaryContainer = Linen,
    inversePrimary = Walnut,
    secondary = Stone,
    onSecondary = Espresso,
    secondaryContainer = DeepUmber,
    onSecondaryContainer = Linen,
    tertiary = Linen,
    onTertiary = Espresso,
    tertiaryContainer = Walnut,
    onTertiaryContainer = Linen,
    background = Espresso,
    onBackground = Linen,
    surface = Espresso,
    onSurface = Linen,
    surfaceVariant = Umber,
    onSurfaceVariant = Stone,
    surfaceTint = Taupe,
    inverseSurface = Linen,
    inverseOnSurface = Espresso,
    outline = Taupe,
    outlineVariant = Walnut,
    surfaceBright = DeepUmber,
    surfaceDim = Espresso,
    surfaceContainerLowest = Color(0xFF1F1307),
    surfaceContainerLow = Bark,
    surfaceContainer = Bark,
    surfaceContainerHigh = Umber,
    surfaceContainerHighest = DeepUmber,
    error = RustLight,
    onError = RustDark,
    errorContainer = RustDark,
    onErrorContainer = RustContainer
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun PlantSpotScoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic colour is off on purpose: the app always uses its own palette.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
