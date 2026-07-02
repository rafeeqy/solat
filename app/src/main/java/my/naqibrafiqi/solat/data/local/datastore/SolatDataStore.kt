package my.naqibrafiqi.solat.data.local.datastore

import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings

interface SolatDataStore {

    fun getTheme(): Flow<ThemeSettings>

    suspend fun setTheme(theme: ThemeSettings)

    fun getZoneCode(): Flow<String?>

    suspend fun setZoneCode(code: String)

    fun getLocale(): Flow<String>

    suspend fun setLocale(locale: String)

    fun getPreviousIsyakPrayer(): Flow<Long?>

    suspend fun setPreviousIsyakPrayer(isyakPrayer: Long)
}
