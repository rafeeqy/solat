package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import my.naqibrafiqi.solat.ui.navigation.route.SettingsRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.settingsNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    composable<SettingsRoutes.SettingsRoute> {
        // TODO: SettingsScreen
    }

    composable<SettingsRoutes.ZoneSelectionRoute> {
        // TODO: ZoneSelectionScreen
    }
}
