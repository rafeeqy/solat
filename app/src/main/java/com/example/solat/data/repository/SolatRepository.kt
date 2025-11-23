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

    fun fetchPrayerTimes(
        year: Int,
        month: Int,
        day: Int
    ): Flow<Map<PrayerDetails, List<PrayerTime>>>
}
