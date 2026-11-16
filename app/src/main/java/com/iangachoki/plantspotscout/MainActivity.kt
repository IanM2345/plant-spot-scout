package com.iangachoki.plantspotscout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.components.AppBottomBar
import com.iangachoki.plantspotscout.ui.components.AppTab
import com.iangachoki.plantspotscout.ui.plants.MyPlantsScreen
import com.iangachoki.plantspotscout.ui.settings.SettingsScreen
import com.iangachoki.plantspotscout.ui.spots.MySpotsScreen
import com.iangachoki.plantspotscout.ui.theme.PlantSpotScoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantSpotScoutTheme {
                PlantSpotScoutApp()
            }
        }
    }
}

@Composable
fun PlantSpotScoutApp(viewModel: SpotsViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // A tab stays selected while a screen inside it is open. Spots is the start tab.
    val selectedTab = AppTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: AppTab.SPOTS

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Each screen's top bar handles the status bar itself, and the bottom bar handles the navigation bar.
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            AppBottomBar(
                selected = selectedTab,
                onSelect = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppTab.SPOTS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppTab.SPOTS.route) { MySpotsScreen(viewModel) }
            composable(AppTab.PLANTS.route) { MyPlantsScreen(viewModel) }
            composable(AppTab.SETTINGS.route) { SettingsScreen(viewModel) }
        }
    }
}
