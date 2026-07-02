package my.naqibrafiqi.solat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import my.naqibrafiqi.solat.ui.navigation.graph.onboardingNavGraph
import my.naqibrafiqi.solat.ui.navigation.graph.prayerNavGraph
import my.naqibrafiqi.solat.ui.navigation.graph.settingsNavGraph
import my.naqibrafiqi.solat.ui.navigation.route.SolatRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

@Composable
fun SolatNavHost(
    startDestination: SolatRoutes,
    snackbarManager: SnackbarManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingNavGraph(snackbarManager, navController)

        prayerNavGraph(snackbarManager, navController)

        settingsNavGraph(snackbarManager, navController)
    }
}
