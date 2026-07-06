package my.naqibrafiqi.solat.domain.model.alarm

import my.naqibrafiqi.solat.domain.model.enums.PrayerName

data class SolatAlarmItem(
    val id: Int,
    val prayerName: PrayerName,
    val triggerAtMillis: Long
)
