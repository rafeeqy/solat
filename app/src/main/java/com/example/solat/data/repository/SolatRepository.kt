package com.example.solat.data.repository

import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.derived.DailyPrayerDetails
import com.example.solat.data.local.entity.relationship.PrayerTimeAndDetails
import kotlinx.coroutines.flow.Flow

interface SolatRepository {
    suspend fun remoteFetchAllZones(): List<Long>

    suspend fun remoteFetchPrayerTimes(
        zone: Zone,
        year: Int,
        month: Int
    ): Map<Long, List<Long>>

    fun fetchAllZones(): Flow<List<Zone>>

    fun fetchZoneById(zoneId: Int): Flow<Zone?>

    fun fetchDailyPrayers(
        zone: Zone
    ): Flow<List<DailyPrayerDetails>>
}
