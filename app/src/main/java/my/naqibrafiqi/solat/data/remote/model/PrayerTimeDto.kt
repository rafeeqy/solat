package my.naqibrafiqi.solat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimeDto(
    val day: Int,
    val hijri: String,
    val imsak: Long,
    val fajr: Long,
    val syuruk: Long,
    val dhuha: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long
)
