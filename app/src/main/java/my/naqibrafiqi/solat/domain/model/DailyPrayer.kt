package my.naqibrafiqi.solat.domain.model

import java.time.LocalDate

data class DailyPrayer(
    val id: Int,
    val zoneId: Int,
    val date: LocalDate,
    val hijri: String,
    val prayerTimes: List<PrayerTime>
)
