package my.naqibrafiqi.solat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import my.naqibrafiqi.solat.ui.theme.SolatTheme
import dagger.hilt.android.AndroidEntryPoint
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingRoutes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolatTheme {
                SolatApp(startDestination = OnboardingRoutes.WelcomeRoute)
            }
        }
    }
}
