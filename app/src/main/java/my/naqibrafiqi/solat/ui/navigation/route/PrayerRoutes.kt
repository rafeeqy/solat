package my.naqibrafiqi.solat.ui.navigation.route

import kotlinx.serialization.Serializable

@Serializable
data object PrayerGraph : SolatRoutes

@Serializable
sealed interface PrayerRoutes : SolatRoutes {

    @Serializable
    data object DailyPrayerRoute : PrayerRoutes
}
