package my.naqibrafiqi.solat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.data.local.db.relationship.DailyPrayerSummary
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity

@Dao
interface SolatDao {
    @Query(
        value = "SELECT * " +
                "FROM zones"
    )
    fun loadAllZones(): Flow<List<ZoneEntity>>

    @Query(
        value = "SELECT * " +
                "FROM zones " +
                "WHERE zone_id = :zoneId AND deleted_date IS NULL"
    )
    fun loadZoneById(zoneId: Int): Flow<ZoneEntity>

    /**
     * Fetch prayers for today and tomorrow.
     *
     * @return list of [DailyPrayerSummary] for today and tomorrow. List size should be __2__.
     */
    @Transaction
    @Query(
        value = "SELECT " +
                "   pd.zone_id, pd.prayer_details_id, pd.year, pd.month_number," +
                "   pt.prayer_time_id, pt.day, pt.fajr, pt.dhuhr, pt.asr, pt.maghrib, pt.isha " +
                "FROM prayer_times pt " +
                "INNER JOIN prayer_details pd ON pd.prayer_details_id = pt.prayer_details_id " +
                "WHERE pd.zone_id = :zoneId AND " +
                "   (" +
                "       (pd.year = :todayYear AND pd.month_number = :todayMonth AND pt.day = :today) OR " +
                "       (pd.year = :tomorrowYear AND pd.month_number = :tomorrowMonth AND pt.day = :tomorrow)" +
                "   )"
    )
    fun loadDailyPrayers(
        zoneId: Int,
        todayYear: Int,
        tomorrowYear: Int,
        todayMonth: Int,
        tomorrowMonth: Int,
        today: Int,
        tomorrow: Int
    ): Flow<List<DailyPrayerSummary>>

    @Transaction
    @Query(
        value = "SELECT " +
                "   pd.zone_id, pd.prayer_details_id, pd.year, pd.month_number," +
                "   pt.prayer_time_id, pt.day, pt.fajr, pt.dhuhr, pt.asr, pt.maghrib, pt.isha " +
                "FROM prayer_times pt " +
                "INNER JOIN prayer_details pd ON pd.prayer_details_id = pt.prayer_details_id " +
                "WHERE pd.year = :year AND pd.month_number = :month AND pt.day = :day"
    )
    fun loadPrayer(
        year: Int,
        month: Int,
        day: Int
    ): DailyPrayerSummary?

    @Query(
        value = "SELECT * " +
                "FROM prayer_details " +
                "WHERE year = :year AND month_number = :month"
    )
    fun hasPrayerOnYearAndMonth(
        year: Int,
        month: Int
    ): PrayerDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertZones(zoneEntities: List<ZoneEntity>): List<Long>

    /**
     * A Dao's transaction method to insert `PrayerDetails` and list of `PrayerTime`.
     *
     * @return a map that contains `PrayerDetails` id as key and list of `PrayerTime` ids as values.
     */
    @Transaction
    suspend fun insertPrayerDetailsAndTimes(
        prayerDetailsEntity: PrayerDetailsEntity,
        prayerTimeEntities: List<PrayerTimeEntity>
    ): Map<Long, List<Long>> {
        return try {
            val prayerDetailsId = insertPrayerDetails(prayerDetailsEntity)
            val newPrayerTimes =
                prayerTimeEntities.map { it.copy(prayerDetailsId = prayerDetailsId.toInt()) }
            val prayerIds = insertPrayerTimes(newPrayerTimes)

            mapOf(prayerDetailsId to prayerIds)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrayerDetails(prayerDetailsEntity: PrayerDetailsEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrayerTimes(prayerTimeEntities: List<PrayerTimeEntity>): List<Long>

    @Query(
        value = "DELETE FROM prayer_details " +
                "WHERE zone_id = :zoneId"
    )
    suspend fun deletePrayerDetailsByZoneId(zoneId: Int)
}
