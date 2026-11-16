package com.iangachoki.plantspotscout.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

/** The three top-level tabs. [route] is the navigation route for each. */
enum class AppTab(val route: String, @StringRes val label: Int, @DrawableRes val icon: Int) {
    SPOTS("spots", R.string.nav_spots, R.drawable.ic_spot),
    PLANTS("plants", R.string.nav_plants, R.drawable.ic_plant),
    SETTINGS("settings", R.string.nav_settings, R.drawable.ic_settings)
}

/**
 * The bottom navigation bar. The selected tab gets a pill behind its icon.
 * NavigationBar adds the navigation bar inset itself.
 */
@Composable
fun AppBottomBar(selected: AppTab, onSelect: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        // No tonal tint and no shadow: the bar is flat.
        tonalElevation = 0.dp
    ) {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                // The label names the tab, so the icon adds nothing for a screen reader.
                icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                label = { Text(stringResource(tab.label), style = MaterialTheme.typography.labelMedium) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Spots selected")
@Composable
private fun AppBottomBarSpotsPreview() {
    PlantSpotScoutTheme { AppBottomBar(selected = AppTab.SPOTS, onSelect = {}) }
}

@Preview(showBackground = true, name = "Plants selected")
@Composable
private fun AppBottomBarPlantsPreview() {
    PlantSpotScoutTheme { AppBottomBar(selected = AppTab.PLANTS, onSelect = {}) }
}

@Preview(showBackground = true, name = "Settings selected")
@Composable
private fun AppBottomBarSettingsPreview() {
    PlantSpotScoutTheme { AppBottomBar(selected = AppTab.SETTINGS, onSelect = {}) }
}
