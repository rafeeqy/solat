package my.naqibrafiqi.solat.domain.model

import java.time.YearMonth

data class PrayerDetails(
    val id: Int,
    val zoneId: Int,
    val yearMonth: YearMonth,
    val dailyPrayers: List<DailyPrayer>
)
