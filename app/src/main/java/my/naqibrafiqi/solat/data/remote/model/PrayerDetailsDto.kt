package my.naqibrafiqi.solat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerDetailsDto(
    val zone: String,
    val year: Int,
    val month: String,
    @SerialName(value = "month_number")
    val monthNumber: Int,
    @SerialName(value = "last_updated")
    val lastUpdated: String? = null,
    val prayers: List<PrayerTimeDto> = emptyList()
)
