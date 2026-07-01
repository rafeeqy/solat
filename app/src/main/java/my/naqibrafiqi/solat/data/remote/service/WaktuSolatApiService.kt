package my.naqibrafiqi.solat.data.remote.service

import my.naqibrafiqi.solat.data.remote.model.PrayerDetailsApiModel
import my.naqibrafiqi.solat.data.remote.model.ZoneApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WaktuSolatApiService {
    @GET(value = "zones")
    suspend fun getAllZones(): List<ZoneApiModel>

    @GET(value = "v2/solat/{zone}")
    suspend fun getPrayerTimes(
        @Path(value = "zone") zone: String,
        @Query(value = "year") year: Int,
        @Query(value = "month") month: Int
    ): PrayerDetailsApiModel
}
