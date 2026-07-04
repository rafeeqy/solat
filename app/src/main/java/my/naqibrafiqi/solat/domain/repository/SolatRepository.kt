package my.naqibrafiqi.solat.domain.repository

import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.util.states.Resource
import java.time.LocalDate

interface SolatRepository {

    fun fetchAllZones(): Flow<Resource<List<Zone>>>

    fun fetchZoneByCode(zoneCode: String): Flow<Zone?>

    fun fetchDailyPrayersForTodayAndTomorrow(
        zoneId: Int,
        zoneCode: String,
        today: LocalDate
    ): Flow<Resource<List<DailyPrayer>>>
}