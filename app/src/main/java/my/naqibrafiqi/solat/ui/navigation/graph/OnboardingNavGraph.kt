package my.naqibrafiqi.solat.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import my.naqibrafiqi.solat.ui.feature.onboarding.welcome.WelcomeScreen
import my.naqibrafiqi.solat.ui.feature.settings.zone.ZoneSelectionScreen
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingGraph
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingRoutes
import my.naqibrafiqi.solat.ui.navigation.route.PrayerRoutes
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

fun NavGraphBuilder.onboardingNavGraph(
    snackbarManager: SnackbarManager,
    navController: NavHostController
) {

    navigation<OnboardingGraph>(startDestination = OnboardingRoutes.WelcomeRoute) {

        composable<OnboardingRoutes.WelcomeRoute> {
            WelcomeScreen(
                onNavigateToZoneSelection = {
                    navController.navigate(route = OnboardingRoutes.ZoneSelectionRoute)
                }
            )
        }

        composable<OnboardingRoutes.ZoneSelectionRoute> {
            ZoneSelectionScreen(
                snackbarManager = snackbarManager,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDailyPrayer = {
                    navController.navigate(PrayerRoutes.DailyPrayerRoute) {
                        popUpTo(OnboardingGraph) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
