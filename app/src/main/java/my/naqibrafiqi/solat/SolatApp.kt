package my.naqibrafiqi.solat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import my.naqibrafiqi.solat.ui.navigation.SolatNavHost
import my.naqibrafiqi.solat.ui.navigation.route.SolatRoutes
import my.naqibrafiqi.solat.util.snackbar.rememberSnackbarState

@Composable
fun SolatApp(startDestination: SolatRoutes) {
    val snackbarManager = rememberSnackbarState()
    val navController = rememberNavController()

    SolatNavHost(
        startDestination = startDestination,
        snackbarManager = snackbarManager,
        navController = navController
    )
}
