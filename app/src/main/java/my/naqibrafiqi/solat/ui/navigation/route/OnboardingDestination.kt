package my.naqibrafiqi.solat.ui.navigation.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import my.naqibrafiqi.solat.ui.screen.onboarding.WelcomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

fun NavGraphBuilder.onboardingNavigation(navController: NavHostController) {
    composable<WelcomeRoute> {
        WelcomeScreen(
            onNavigateToZoneLocation = {
                navController.navigateToZoneLocation()
            },
            onNavigateToMain = {
                navController.navigate(MainRoute) {
                    popUpTo(WelcomeRoute) { inclusive = true }
                }
            }
        )
    }
}
