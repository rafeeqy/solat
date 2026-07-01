package my.naqibrafiqi.solat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import my.naqibrafiqi.solat.ui.navigation.SolatNavHost

@Composable
fun SolatApp() {
    val navController = rememberNavController()

    SolatNavHost(navController)
}
