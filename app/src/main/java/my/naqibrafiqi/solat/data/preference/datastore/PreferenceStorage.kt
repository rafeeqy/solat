package my.naqibrafiqi.solat.data.preference.datastore

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    fun getCurrentZoneId(): Flow<Int>

    suspend fun setCurrentZoneId(zoneId: Int)

    fun getZoneCode(): Flow<String?>

    suspend fun setZoneCode(code: String)

    fun getTheme(): Flow<String>

    suspend fun setTheme(theme: String)

    fun getLocale(): Flow<String>

    suspend fun setLocale(locale: String)

    fun getPreviousIsyakPrayer(): Flow<Long?>

    suspend fun setPreviousIsyakPrayer(isyakPrayer: Long)
}
