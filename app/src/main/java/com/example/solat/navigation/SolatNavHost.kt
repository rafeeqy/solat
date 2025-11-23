package com.example.solat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.solat.navigation.route.MainRoute
import com.example.solat.navigation.route.mainNavigation

@Composable
fun SolatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute,
        modifier = modifier
    ) {
        mainNavigation(navController)
    }
}
