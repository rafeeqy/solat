package com.example.solat.data.repository

import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
import kotlinx.coroutines.flow.Flow

interface SolatRepository {
    suspend fun remoteFetchAllZones(): List<Long>

    suspend fun remoteFetchPrayerTimes(
        zone: Zone,
        year: Int,
        month: Int
    ): Map<Long, List<Long>>

    fun fetchAllZones(): Flow<List<Zone>>

    fun fetchZoneById(zoneId: Int): Zone

    fun fetchPrayerTimes(
        zoneId: Int,
        year: Int,
        month: Int,
        day: Int,
    ): Flow<PrayerTime?>
}
