package com.example.solat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.derived.DailyPrayerDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SolatDao {
    @Query(
        value = "SELECT * " +
                "FROM zone"
    )
    fun loadAllZones(): Flow<List<Zone>>

    @Query(
        value = "SELECT * " +
                "FROM zone " +
                "WHERE zone_id = :zoneId AND deleted_date IS NULL"
    )
    fun loadZoneById(zoneId: Int): Flow<Zone>

    /**
     * [year], [month], and [day] should has only __2__ values that represent next year/month/day.
     *
     * @return list of [DailyPrayerDetails] for today and tomorrow. List size should be __2__.
     */
    @Transaction
    @Query(
        value = "SELECT " +
                "   pd.zone_id, pd.prayer_details_id, pd.year, pd.month_number," +
                "   pt.prayer_time_id, pt.day, pt.fajr, pt.dhuhr, pt.asr, pt.maghrib, pt.isha " +
                "FROM prayer_time pt " +
                "INNER JOIN prayer_details pd ON pd.prayer_details_id = pt.prayer_details_id " +
                "WHERE pd.zone_id = :zoneId AND pd.year IN (:year) AND pd.month_number IN (:month) AND pt.day IN (:day) "
    )
    fun loadDailyPrayers(
        zoneId: Int,
        year: List<Int>,
        month: List<Int>,
        day: List<Int>
    ): Flow<List<DailyPrayerDetails>>

    @Query(
        value = "SELECT * " +
                "FROM prayer_details " +
                "WHERE year = :year AND month_number = :month"
    )
    suspend fun hasPrayerOnYearAndMonth(
        year: Int,
        month: Int
    ): PrayerDetails?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertZones(zones: List<Zone>): List<Long>

    /**
     * A Dao's transaction method to insert `PrayerDetails` and list of `PrayerTime`.
     *
     * @return a map that contains `PrayerDetails` id as key and list of `PrayerTime` ids as values.
     */
    @Transaction
    suspend fun insertPrayerDetailsAndTimes(
        prayerDetails: PrayerDetails,
        prayerTimes: List<PrayerTime>
    ): Map<Long, List<Long>> {
        return try {
            val prayerDetailsId = insertPrayerDetails(prayerDetails)
            val newPrayerTimes =
                prayerTimes.map { it.copy(prayerDetailsId = prayerDetailsId.toInt()) }
            val prayerIds = insertPrayerTimes(newPrayerTimes)

            mapOf(prayerDetailsId to prayerIds)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrayerDetails(prayerDetails: PrayerDetails): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrayerTimes(prayerTimes: List<PrayerTime>): List<Long>
}
