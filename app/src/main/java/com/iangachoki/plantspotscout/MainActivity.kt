package com.iangachoki.plantspotscout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iangachoki.plantspotscout.ui.SpotsViewModel
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

private enum class Tab(val route: String, @StringRes val label: Int, @DrawableRes val icon: Int) {
    SPOTS("spots", R.string.tab_my_spots, R.drawable.ic_spot),
    PLANTS("plants", R.string.tab_my_plants, R.drawable.ic_plant),
    SETTINGS("settings", R.string.tab_settings, R.drawable.ic_settings)
}

@Composable
fun PlantSpotScoutApp(viewModel: SpotsViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Tab.SPOTS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Tab.SPOTS.route) { MySpotsScreen(viewModel) }
            composable(Tab.PLANTS.route) { MyPlantsScreen(viewModel) }
            composable(Tab.SETTINGS.route) { SettingsScreen(viewModel) }
        }
    }
}
