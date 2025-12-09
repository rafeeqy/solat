package com.example.solat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.solat.navigation.route.MainRoute
import com.example.solat.navigation.route.WelcomeRoute
import com.example.solat.navigation.route.mainNavigation
import com.example.solat.navigation.route.onboardingNavigation
import com.example.solat.ui.viewmodel.OnboardingViewModel

@Composable
fun SolatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val onboardingVM: OnboardingViewModel = hiltViewModel()

    val isFirstTime: Boolean = onboardingVM.currentZoneId.collectAsStateWithLifecycle().value == 0

    NavHost(
        navController = navController,
        startDestination = if (isFirstTime) WelcomeRoute else MainRoute,
        modifier = modifier
    ) {
        onboardingNavigation(navController)
        mainNavigation(navController)
    }
}
