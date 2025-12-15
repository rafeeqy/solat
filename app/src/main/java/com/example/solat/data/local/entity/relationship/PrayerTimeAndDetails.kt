package com.example.solat.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime

data class PrayerTimeAndDetails(
    @Embedded
    val time: PrayerTime,
    @Relation(
        parentColumn = "prayer_details_id",
        entityColumn = "prayer_details_id"
    )
    val details: PrayerDetails
)
