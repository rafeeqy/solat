package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import my.naqibrafiqi.solat.ui.navigation.route.PrayerRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.prayerNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    composable<PrayerRoutes.DailyPrayerRoute> {
        // TODO: DailyPrayerScreen
    }
}
