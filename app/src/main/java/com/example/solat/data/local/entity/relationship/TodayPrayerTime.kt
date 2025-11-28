package com.example.solat.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone

data class TodayPrayerTime(
    @Embedded
    val time: PrayerTime,
    @Relation(
        entity = PrayerDetails::class,
        parentColumn = "prayer_details_id",
        entityColumn = "prayer_details_id"
    )
    val prayerDetails: PrayerDetailsAndZone
)
