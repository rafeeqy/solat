package com.example.solat.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.Zone

data class PrayerDetailsAndZone(
    @Embedded
    val details: PrayerDetails,
    @Relation(
        parentColumn = "zone_id",
        entityColumn = "zone_id"
    )
    val zone: Zone
)
