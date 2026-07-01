package my.naqibrafiqi.solat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import my.naqibrafiqi.solat.ui.navigation.route.MainRoute
import my.naqibrafiqi.solat.ui.navigation.route.WelcomeRoute
import my.naqibrafiqi.solat.ui.navigation.route.mainNavigation
import my.naqibrafiqi.solat.ui.navigation.route.onboardingNavigation
import my.naqibrafiqi.solat.ui.viewmodel.OnboardingViewModel

@Composable
fun SolatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val onboardingVM: OnboardingViewModel = hiltViewModel()
    val zoneId = onboardingVM.currentZoneId.collectAsStateWithLifecycle().value

    var isFirstTime = false

    LaunchedEffect(zoneId) {
        isFirstTime = zoneId == 0
    }

    NavHost(
        navController = navController,
        startDestination = if (isFirstTime) WelcomeRoute else MainRoute,
        modifier = modifier
    ) {
        onboardingNavigation(navController)
        mainNavigation(navController)
    }
}
