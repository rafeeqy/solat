package my.naqibrafiqi.solat.data.local.db.relationship

import androidx.room.Embedded
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity

data class PrayerDetailsAndTime(
    @Embedded
    val details: PrayerDetailsEntity,
    @Embedded(prefix = "pt_")
    val time: PrayerTimeEntity
)