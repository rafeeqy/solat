package my.naqibrafiqi.solat.data.remote

import my.naqibrafiqi.solat.data.remote.model.PrayerDetailsDto
import my.naqibrafiqi.solat.data.remote.model.ZoneDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WaktuSolatApiService {
    @GET(value = "zones")
    suspend fun getAllZones(): List<ZoneDto>

    @GET(value = "v2/solat/{zone}")
    suspend fun getPrayerTimes(
        @Path(value = "zone") zone: String,
        @Query(value = "year") year: Int,
        @Query(value = "month") month: Int
    ): PrayerDetailsDto
}