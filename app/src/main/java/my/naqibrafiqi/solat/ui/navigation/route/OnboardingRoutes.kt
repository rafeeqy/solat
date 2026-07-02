package my.naqibrafiqi.solat.ui.navigation.route

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraph : SolatRoutes

@Serializable
sealed interface OnboardingRoutes : SolatRoutes {

    @Serializable
    data object WelcomeRoute : OnboardingRoutes

    @Serializable
    data object ZoneSelectionRoute : OnboardingRoutes
}
