package my.naqibrafiqi.solat.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import my.naqibrafiqi.solat.data.local.entity.PrayerDetails
import my.naqibrafiqi.solat.data.local.entity.PrayerTime

data class PrayerTimeAndDetails(
    @Embedded
    val time: PrayerTime,
    @Relation(
        parentColumn = "prayer_details_id",
        entityColumn = "prayer_details_id"
    )
    val details: PrayerDetails
)
