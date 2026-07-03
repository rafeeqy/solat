package my.naqibrafiqi.solat.domain.repository

import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.repository.Prayer
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.util.states.Resource

interface SolatRepository {

    fun fetchAllZones(): Flow<Resource<List<Zone>>>

    suspend fun remoteFetchAllZones(): List<Long>

    suspend fun remoteFetchPrayerTimes(
        zoneId: Int,
        zoneCode: String,
        year: Int,
        month: Int
    ): Map<Long, List<Long>>

    fun fetchZoneById(zoneId: Int): Flow<ZoneEntity?>

    fun fetchDailyPrayers(
        zoneEntity: ZoneEntity
    ): Flow<List<Prayer>>

    fun fetchCurrentPrayer(
        zoneId: Int,
        zoneCode: String,
        date: Long
    ): Prayer?

    suspend fun deletePrayerByZone(zoneId: Int)
}