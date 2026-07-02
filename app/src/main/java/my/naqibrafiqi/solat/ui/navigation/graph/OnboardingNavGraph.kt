package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.onboardingNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    composable<OnboardingRoutes.WelcomeRoute> {
        // TODO: WelcomeScreen
    }

    composable<OnboardingRoutes.ZoneSelectionRoute> {
        // TODO: ZoneSelectionScreen
    }
}
