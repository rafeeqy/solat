package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import my.naqibrafiqi.solat.ui.feature.settings.SettingsScreen
import my.naqibrafiqi.solat.ui.feature.settings.notification.NotificationScreen
import my.naqibrafiqi.solat.ui.feature.settings.zone.ZoneSelectionScreen
import my.naqibrafiqi.solat.ui.navigation.route.SettingsGraph
import my.naqibrafiqi.solat.ui.navigation.route.SettingsRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.settingsNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    navigation<SettingsGraph>(startDestination = SettingsRoutes.SettingsRoute) {

        composable<SettingsRoutes.SettingsRoute> {
            SettingsScreen(
                snackbarManager = snackbarManager,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToZoneLocation = {
                    navController.navigate(route = SettingsRoutes.ZoneSelectionRoute)
                },
                onNavigateToNotification = {
                    navController.navigate(route = SettingsRoutes.NotificationRoute)
                }
            )
        }

        composable<SettingsRoutes.ZoneSelectionRoute> {
            ZoneSelectionScreen(
                snackbarManager = snackbarManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoutes.NotificationRoute> {
            NotificationScreen(
                snackbarManager = snackbarManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
