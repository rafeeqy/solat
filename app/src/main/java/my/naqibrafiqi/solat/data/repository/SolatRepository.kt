package my.naqibrafiqi.solat.data.repository

import my.naqibrafiqi.solat.data.local.entity.Zone
import kotlinx.coroutines.flow.Flow

interface SolatRepository {
    suspend fun remoteFetchAllZones(): List<Long>

    suspend fun remoteFetchPrayerTimes(
        zoneId: Int,
        zoneCode: String,
        year: Int,
        month: Int
    ): Map<Long, List<Long>>

    fun fetchAllZones(): Flow<List<Zone>>

    fun fetchZoneById(zoneId: Int): Flow<Zone?>

    fun fetchDailyPrayers(
        zone: Zone
    ): Flow<List<Prayer>>

    fun fetchCurrentPrayer(
        zoneId: Int,
        zoneCode: String,
        date: Long
    ): Prayer?

    suspend fun deletePrayerByZone(zoneId: Int)
}
