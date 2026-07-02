package my.naqibrafiqi.solat.data.local.db.relationship

import androidx.room.Embedded
import androidx.room.Relation
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity

data class PrayerDetailsAndTime(
    @Embedded
    val details: PrayerDetailsEntity,
    @Relation(
        parentColumn = "prayer_details_id",
        entityColumn = "prayer_details_id"
    )
    val time: PrayerTimeEntity
)