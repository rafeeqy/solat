package my.naqibrafiqi.solat.domain.model

import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import java.time.LocalTime

data class PrayerTime(
    val prayer: PrayerName,
    val time: LocalTime,
    val tableRowName: String
)
