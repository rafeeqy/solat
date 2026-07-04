package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import my.naqibrafiqi.solat.ui.navigation.route.SettingsGraph
import my.naqibrafiqi.solat.ui.navigation.route.SettingsRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.settingsNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    navigation<SettingsGraph>(startDestination = SettingsRoutes.SettingsRoute) {
        
        composable<SettingsRoutes.SettingsRoute> {
            // TODO: SettingsScreen
        }

        composable<SettingsRoutes.ZoneSelectionRoute> {
            // TODO: ZoneSelectionScreen
        }
    }
}
