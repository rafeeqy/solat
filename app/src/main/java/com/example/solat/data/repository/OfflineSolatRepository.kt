package com.example.solat.data.repository

import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.util.SolatDateTime
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineSolatRepository @Inject constructor(
    private val waktuSolatApiService: WaktuSolatApiService,
    private val solatDao: SolatDao
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

    override fun fetchZoneById(zoneId: Int): Zone =
        solatDao.loadZoneById(zoneId)

    /**
     * Fetch `PrayerDetails` and 2 items of `PrayerTime` for today and tomorrow.
     */
    override fun fetchPrayerTimes(
        zoneId: Int,
        year: Int,
        month: Int,
        day: Int
    ): Flow<PrayerTime?> =
        solatDao.loadPrayerTimes(zoneId, year, month, day)
}
