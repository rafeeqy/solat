package my.naqibrafiqi.solat.domain.model.alarm

data class SolatAlarmItem(
    val id: Int,
    val prayerName: String,
    val triggerAtMillis: Long
)
