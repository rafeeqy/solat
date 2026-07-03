package my.naqibrafiqi.solat

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingRoutes
import my.naqibrafiqi.solat.ui.theme.SolatTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        setContent {
            val theme = mainViewModel.theme.collectAsStateWithLifecycle().value

            val isDarkTheme = when (theme) {
                ThemeSettings.SYSTEM -> isSystemInDarkTheme()
                ThemeSettings.LIGHT -> false
                ThemeSettings.DARK -> true
            }

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(
                            Color.Transparent.toArgb(),
                            Color.Transparent.toArgb()
                        )
                    }
                )

                onDispose {}
            }

            SolatTheme(darkTheme = isDarkTheme) {
                if (!mainViewModel.isLoading.value) {
                    SolatApp(startDestination = mainViewModel.startDestination.value)
                }
            }
        }
    }
}
