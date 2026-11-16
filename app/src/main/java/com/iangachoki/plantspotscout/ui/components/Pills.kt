package com.iangachoki.plantspotscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.LightBand
import com.iangachoki.plantspotscout.model.LightFit
import com.iangachoki.plantspotscout.ui.theme.containerColor
import com.iangachoki.plantspotscout.ui.theme.contentColor

/** Dot colour for each band: the band's own container colour from the design. */
fun LightBand.dotColor(): Color = containerColor()

/**
 * A "Light: <band>" pill filled with the band colour. The band name is always written out,
 * so the meaning never depends on colour alone.
 */
@Composable
fun LightBandChip(band: LightBand, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = band.containerColor(),
        contentColor = band.contentColor(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(band.dotColor())
                    // The ring is the text colour so the dot stays visible on the same-coloured chip.
                    .border(1.dp, band.contentColor(), CircleShape)
            )
            Text(
                stringResource(R.string.light_band, band.label),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

/** A badge saying how well a spot suits a plant. A good fit is filled, a poor fit is outlined in the error colour. */
@Composable
fun FitBadge(fit: LightFit, modifier: Modifier = Modifier) {
    val good = fit == LightFit.GOOD
    Surface(
        shape = CircleShape,
        color = if (good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer,
        contentColor = if (good) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier
    ) {
        Text(
            fit.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** The large serif title at the top of each tab. */
@Composable
fun ScreenHeader(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier = modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** A centred icon and message for an empty list. */
@Composable
fun EmptyState(iconRes: Int, message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
