package com.example.solat.data.repository

import android.util.Log
import androidx.work.WorkManager
import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.derived.DailyPrayerDetails
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.util.NextDayStatus
import com.example.solat.util.SolatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OfflineSolatRepository @Inject constructor(
    private val waktuSolatApiService: WaktuSolatApiService,
    private val solatDao: SolatDao,
    private val workManager: WorkManager
) : SolatRepository {
    override suspend fun remoteFetchAllZones(): List<Long> {
        val zones = waktuSolatApiService.getAllZones()

        return solatDao.insertZones(
            zones.map { zone ->
                Zone(
                    code = zone.jakimCode,
                    state = zone.negeri,
                    district = zone.daerah,
                    createdDate = SolatDateTime.nowEpochMilli()
                )
            }
        )
    }

    override suspend fun remoteFetchPrayerTimes(
        zone: Zone,
        year: Int,
        month: Int
    ): Map<Long, List<Long>> {
        val prayer = waktuSolatApiService.getPrayerTimes(zone.code, year, month)

        return solatDao.insertPrayerDetailsAndTimes(
            prayerDetails = PrayerDetails(
                zoneId = zone.zoneId,
                year = prayer.year,
                month = prayer.month,
                monthNumber = prayer.monthNumber,
                apiLastUpdated = prayer.lastUpdated,
                createdDate = SolatDateTime.nowEpochMilli()
            ),
            prayerTimes = prayer.prayers.map { time ->
                PrayerTime(
                    prayerDetailsId = 0,
                    day = time.day,
                    hijri = time.hijri,
                    fajr = time.fajr,
                    syuruk = time.syuruk,
                    dhuhr = time.dhuhr,
                    asr = time.asr,
                    maghrib = time.maghrib,
                    isha = time.isha,
                    createdDate = SolatDateTime.nowEpochMilli()
                )
            }
        )
    }

    override fun fetchAllZones(): Flow<List<Zone>> =
        solatDao.loadAllZones()

    override fun fetchZoneById(zoneId: Int): Flow<Zone?> {
        return if (zoneId > 0) solatDao.loadZoneById(zoneId) else flowOf(null)
    }

    override fun fetchDailyPrayers(
        zone: Zone
    ): Flow<List<DailyPrayerDetails>> = flow {
        val today = SolatDateTime.splitDate(SolatDateTime.nowEpochMilli())
        val tomorrow = SolatDateTime.splitDate(SolatDateTime.tomorrowEpochMilli())
        val tomorrowStatus = SolatDateTime.nextDayStatus()

        if (
            tomorrowStatus != NextDayStatus.NO_CHANGES &&
            solatDao.hasPrayerOnYearAndMonth(tomorrow.year, tomorrow.month) == null
        ) {
            try {
                remoteFetchPrayerTimes(zone, tomorrow.year, tomorrow.month)
            } catch (e: Exception) {
                Log.e("FETCHDAILYPRAYERS", "Error: ${e.message}")
            }
        }

        emitAll(
            solatDao.loadDailyPrayers(
                zone.zoneId,
                listOf(today.year, tomorrow.year),
                listOf(today.month, tomorrow.month),
                listOf(today.day, tomorrow.day)
            )
        )
    }.flowOn(context = Dispatchers.IO)
}
