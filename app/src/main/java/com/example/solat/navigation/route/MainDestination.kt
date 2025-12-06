package com.example.solat.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.solat.ui.screen.MainScreen
import com.example.solat.ui.screen.SettingsScreen
import com.example.solat.ui.screen.ZoneLocationScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

@Serializable
data object ZoneLocationRoute

@Serializable
data object SettingsRoute

fun NavGraphBuilder.mainNavigation(navController: NavHostController) {
    composable<MainRoute> {
        MainScreen(
            onNavigateToSettings = { navController.navigateToSettings() },
            onNavigateToZoneLocation = { navController.navigateToZoneLocation() }
        )
    }

    composable<ZoneLocationRoute> {
        ZoneLocationScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<SettingsRoute> {
        SettingsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToZoneLocation = { navController.navigateToZoneLocation() }
        )
    }
}

fun NavController.navigateToZoneLocation() {
    navigate(ZoneLocationRoute)
}

fun NavController.navigateToSettings() {
    navigate(SettingsRoute)
}
