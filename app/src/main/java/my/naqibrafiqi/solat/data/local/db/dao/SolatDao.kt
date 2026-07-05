package my.naqibrafiqi.solat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query(
        value = """
            DELETE FROM prayer_details 
            WHERE year = :year AND month_number = :month
        """
    )
    suspend fun deleteAllPastPrayerTimes(year: Int, month: Int): Int

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
            LEFT JOIN prayer_times pt ON pt.prayer_details_id = pd.prayer_details_id AND pt.day IN (:todayDay, :tomorrowDay) AND pt.deleted_date IS NULL 
            WHERE pd.zone_id = :zoneId AND pd.year IN (:todayYear, :tomorrowYear) AND pd.month_number IN (:todayMonth, :tomorrowMonth)AND pd.deleted_date IS NULL 
            ORDER BY pd.year, pd.month_number, pt.day
        """
    )
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
