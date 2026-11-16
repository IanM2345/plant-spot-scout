package com.iangachoki.plantspotscout.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontEntry
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.iangachoki.plantspotscout.R

// Figtree is downloaded through Google Play services. Until it arrives, or if it fails, text uses the system font.
private val FontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val FigtreeName = GoogleFont("Figtree")

// Only the three weights the design uses.
val Figtree = FontFamily(
    GoogleFontEntry(googleFont = FigtreeName, fontProvider = FontProvider, weight = FontWeight.Normal),
    GoogleFontEntry(googleFont = FigtreeName, fontProvider = FontProvider, weight = FontWeight.Medium),
    GoogleFontEntry(googleFont = FigtreeName, fontProvider = FontProvider, weight = FontWeight.SemiBold)
)

// Slots the design does not define keep Material's default sizes but still use Figtree, so no text falls back to the system font.
private val Base = Typography().let {
    Typography(
        displayLarge = it.displayLarge.copy(fontFamily = Figtree),
        displayMedium = it.displayMedium.copy(fontFamily = Figtree),
        displaySmall = it.displaySmall.copy(fontFamily = Figtree),
        headlineLarge = it.headlineLarge.copy(fontFamily = Figtree),
        headlineSmall = it.headlineSmall.copy(fontFamily = Figtree),
        titleSmall = it.titleSmall.copy(fontFamily = Figtree),
        bodySmall = it.bodySmall.copy(fontFamily = Figtree),
        labelSmall = it.labelSmall.copy(fontFamily = Figtree)
    )
}

val Typography = Base.copy(
    // Design style "Headline": screen titles.
    headlineMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    // Design style "Title": section titles and the spot name.
    titleLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    // Design style "Title small": card titles. The design gives no line height, so it is 24 sp to match Body.
    titleMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Design style "Body": main text. "tnum" makes digits the same width so lux numbers line up.
    bodyLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFeatureSettings = "tnum"
    ),
    // Design style "Supporting": text under card titles.
    bodyMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    // Design style "Label": chips and nav labels.
    labelMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Design style "Button text".
    labelLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)
