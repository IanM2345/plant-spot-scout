package com.iangachoki.plantspotscout.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme
import com.iangachoki.plantspotscout.ui.theme.Spacing

/**
 * Keeps bar content out of the status bar, and out of a display cutout when the phone is turned sideways.
 * Every bar in this file applies it, so a screen must not add the status bar padding a second time.
 */
@Composable
private fun Modifier.topBarInsets(): Modifier =
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))

/** A title with an optional subtitle under it. Shared by the bars that show both. */
@Composable
private fun TitleBlock(
    title: String,
    subtitle: String?,
    titleStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            title,
            style = titleStyle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics { heading() }
        )
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * A plain icon button for a bar. The 48 dp tap area meets the minimum touch target.
 * [contentDescription] is required because the icon is the only thing a screen reader can announce.
 */
@Composable
fun TopBarIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(Spacing.minTouchTarget),
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Icon(painterResource(icon), contentDescription = contentDescription)
    }
}

/** A text button for a bar, such as "Skip" on onboarding. */
@Composable
fun TopBarTextAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = Spacing.minTouchTarget),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * The big title at the top of a tab (My spots, My plants, Settings).
 * It sits on the screen background with no elevation and no divider.
 */
@Composable
fun LargeTitleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .topBarInsets()
            .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleBlock(
            title = title,
            subtitle = subtitle,
            titleStyle = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

/** A bar with a back arrow on the left, for screens opened from another screen. */
@Composable
fun BackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .topBarInsets()
            .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarIconButton(R.drawable.ic_arrow_back, stringResource(R.string.back), onBack)
        TitleBlock(
            title = title,
            subtitle = subtitle,
            titleStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.sm)
        )
        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

/** A bar with a close (X) icon on the left, for full-screen forms. */
@Composable
fun CloseTopBar(title: String, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .topBarInsets()
            .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarIconButton(R.drawable.ic_close, stringResource(R.string.close), onClose)
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.sm)
                .semantics { heading() }
        )
    }
}

/**
 * A round button for [PhotoHeaderTopBar]: a surface-coloured circle with the icon in onSurface,
 * so it stays visible on any photo. The circle is 40 dp inside a 48 dp tap area.
 */
@Composable
fun PhotoHeaderAction(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier.size(Spacing.minTouchTarget)) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(Spacing.photoButton)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(painterResource(icon), contentDescription = contentDescription)
            }
        }
    }
}

/**
 * A see-through bar drawn over a photo: back on the left, [actions] on the right.
 * Put it in a Box on top of the photo. It adds the status bar padding itself.
 */
@Composable
fun PhotoHeaderTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .topBarInsets()
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhotoHeaderAction(R.drawable.ic_arrow_back, stringResource(R.string.back), onBack)
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8)
@Composable
private fun LargeTitleTopBarPreview() {
    PlantSpotScoutTheme {
        LargeTitleTopBar(
            title = "My spots",
            subtitle = "4 spots measured · window reference set 2 days ago",
            actions = { TopBarIconButton(R.drawable.ic_add, "Add", onClick = {}) }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8)
@Composable
private fun LargeTitleTopBarNoSubtitlePreview() {
    PlantSpotScoutTheme { LargeTitleTopBar(title = "Settings") }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8)
@Composable
private fun BackTopBarPreview() {
    PlantSpotScoutTheme {
        BackTopBar(title = "Kitchen sill", subtitle = "Measuring · morning slot", onBack = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8)
@Composable
private fun CloseTopBarPreview() {
    PlantSpotScoutTheme { CloseTopBar(title = "New spot", onClose = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8)
@Composable
private fun TopBarTextActionPreview() {
    PlantSpotScoutTheme { TopBarTextAction(text = "Skip", onClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun PhotoHeaderTopBarPreview() {
    PlantSpotScoutTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
            PhotoHeaderTopBar(
                onBack = {},
                actions = {
                    PhotoHeaderAction(R.drawable.ic_camera, "Take photo", onClick = {})
                    PhotoHeaderAction(R.drawable.ic_share, "Share", onClick = {})
                    PhotoHeaderAction(R.drawable.ic_more_vert, "More options", onClick = {})
                }
            )
        }
    }
}
