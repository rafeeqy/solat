package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import my.naqibrafiqi.solat.ui.feature.prayer.DailyPrayerScreen
import my.naqibrafiqi.solat.ui.navigation.route.PrayerGraph
import my.naqibrafiqi.solat.ui.navigation.route.PrayerRoutes
import my.naqibrafiqi.solat.ui.navigation.route.SettingsGraph
import my.naqibrafiqi.solat.ui.navigation.route.SettingsRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.prayerNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    navigation<PrayerGraph>(startDestination = PrayerRoutes.DailyPrayerRoute) {

        composable<PrayerRoutes.DailyPrayerRoute>(
            deepLinks = listOf(
                navDeepLink<PrayerRoutes.DailyPrayerRoute>(
                    basePath = "solat://prayer/daily"
                )
            )
        ) {
            DailyPrayerScreen(
                snackbarManager = snackbarManager,
                onNavigateToSettings = {
                    navController.navigate(route = SettingsGraph)
                },
                onNavigateToZoneLocation = {
                    navController.navigate(route = SettingsRoutes.ZoneSelectionRoute)
                }
            )
        }
    }
}
