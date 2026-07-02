package my.naqibrafiqi.solat.data.local.db.relationship

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class DailyPrayerSummary(
    @ColumnInfo("zone_id")
    val zoneId: Int,
    @ColumnInfo("prayer_details_id")
    val prayerDetailsId: Int,
    @ColumnInfo("prayer_time_id")
    val prayerTimeId: Int,
    val year: Int,
    @ColumnInfo("month_number")
    val month: Int,
    val day: Int,
    val fajr: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long
)