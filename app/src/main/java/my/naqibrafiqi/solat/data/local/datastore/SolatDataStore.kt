package my.naqibrafiqi.solat.data.local.datastore

import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings

interface SolatDataStore {

    fun getTheme(): Flow<ThemeSettings>

    suspend fun setTheme(theme: ThemeSettings)

    fun getZoneCode(): Flow<String?>

    suspend fun setZoneCode(code: String)

    fun getEnabledPrayers(): Flow<Set<String>>

    suspend fun togglePrayerNotification(prayerName: String, isEnabled: Boolean)
}
