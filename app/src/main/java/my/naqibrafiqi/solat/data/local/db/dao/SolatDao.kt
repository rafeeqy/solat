package my.naqibrafiqi.solat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.local.db.relationship.PrayerDetailsAndTime

@Dao
interface SolatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zoneEntities: List<ZoneEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerDetails(prayerDetailsEntity: PrayerDetailsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayerTimeEntities: List<PrayerTimeEntity>): List<Long>

    /**
     * Saves one month of prayer times. The month's details and its days are written together, so
     * a month is never left saved without its days. Saving a month that's already saved replaces
     * it (and, through the foreign key, its old days).
     */
    @Transaction
    suspend fun insertMonthlyPrayers(
        prayerDetailsEntity: PrayerDetailsEntity,
        prayerTimeEntities: List<PrayerTimeEntity>
    ) {
        val prayerDetailsId = insertPrayerDetails(prayerDetailsEntity).toInt()

        insertPrayerTimes(prayerTimeEntities.map { it.copy(prayerDetailsId = prayerDetailsId) })
    }

    /** Deletes every month before [year]/[month], with its days. */
    @Query(
        value = """
            DELETE FROM prayer_details 
            WHERE (year * 12 + month_number) < (:year * 12 + :month)
        """
    )
    suspend fun deletePrayerTimesBefore(year: Int, month: Int): Int

    @Query(
        value = """
            SELECT * 
            FROM zones 
            WHERE deleted_date IS NULL
        """
    )
    fun loadAllZones(): Flow<List<ZoneEntity>>

    @Query(
        value = """
            SELECT * 
            FROM zones 
            WHERE code = :code AND deleted_date IS NULL
        """
    )
    fun loadZoneByCode(code: String): Flow<ZoneEntity?>

    @Query(
        value = """
            SELECT 
                pd.*, 
                pt.prayer_time_id AS pt_prayer_time_id, 
                pt.prayer_details_id AS pt_prayer_details_id, 
                pt.day AS pt_day, 
                pt.hijri AS pt_hijri, 
                pt.imsak AS pt_imsak, 
                pt.fajr AS pt_fajr, 
                pt.syuruk AS pt_syuruk, 
                pt.dhuha AS pt_dhuha, 
                pt.dhuhr AS pt_dhuhr, 
                pt.asr AS pt_asr, 
                pt.maghrib AS pt_maghrib, 
                pt.isha AS pt_isha, 
                pt.created_date AS pt_created_date, 
                pt.updated_date AS pt_updated_date, 
                pt.deleted_date AS pt_deleted_date
            FROM prayer_details pd 
            INNER JOIN prayer_times pt ON pt.prayer_details_id = pd.prayer_details_id 
            WHERE pd.zone_id = :zoneId 
                AND pd.deleted_date IS NULL 
                AND pt.deleted_date IS NULL 
                AND (
                    (pd.year = :todayYear AND pd.month_number = :todayMonth AND pt.day = :todayDay) 
                    OR (pd.year = :tomorrowYear AND pd.month_number = :tomorrowMonth AND pt.day = :tomorrowDay)
                ) 
            ORDER BY pd.year, pd.month_number, pt.day
        """
    )
    /**
     * Today's and tomorrow's prayer times, in date order: two rows when both days are saved.
     * Each day is matched by its full date, so on the last day of a month "tomorrow" is the 1st of
     * the next month (not the 1st of the same month).
     */
    fun loadPrayerForTodayAndTomorrow(
        zoneId: Int,
        todayYear: Int,
        todayMonth: Int,
        todayDay: Int,
        tomorrowYear: Int,
        tomorrowMonth: Int,
        tomorrowDay: Int
    ): Flow<List<PrayerDetailsAndTime>>
}
