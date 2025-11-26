package com.example.solat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.relationship.TodayPrayerTime
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

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
     * A Dao method to fetch prayer times with details.
     *
     * @return a map that contains `PrayerDetails` and 2 items of `PrayerTime` for today and tomorrow.
     */
    @Query(
        value = "SELECT * " +
                "FROM prayer_details pd " +
                "LEFT JOIN zone z ON z.zone_id = pd.zone_id " +
                "LEFT JOIN prayer_time pt ON pt.prayer_details_id = pd.prayer_details_id " +
                "WHERE pd.zone_id = :zoneId AND " +
                "   pd.year = :year AND pd.month_number = :month AND pt.day = :day AND " +
                "   pd.deleted_date IS NULL"
    )
    fun loadPrayerTimes(
        zoneId: Int,
        year: Int,
        month: Int,
        day: Int
    ): Flow<TodayPrayerTime?>

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
