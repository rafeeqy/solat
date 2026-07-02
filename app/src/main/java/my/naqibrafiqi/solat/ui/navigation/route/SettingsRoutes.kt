package my.naqibrafiqi.solat.ui.navigation.route

import kotlinx.serialization.Serializable

@Serializable
data object SettingsGraph : SolatRoutes

@Serializable
sealed interface SettingsRoutes : SolatRoutes {

    @Serializable
    data object SettingsRoute : SettingsRoutes

    @Serializable
    data object ZoneSelectionRoute : SettingsRoutes
}
