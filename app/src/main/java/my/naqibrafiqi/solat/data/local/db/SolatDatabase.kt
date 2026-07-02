package my.naqibrafiqi.solat.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity

@Database(
    entities = [
        ZoneEntity::class,
        PrayerDetailsEntity::class,
        PrayerTimeEntity::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
abstract class SolatDatabase : RoomDatabase() {
    abstract fun solatDao(): SolatDao

    companion object {
        private var Instance: SolatDatabase? = null

        fun getDatabase(context: Context): SolatDatabase {
            return Instance ?: synchronized(lock = this) {
                Room
                    .databaseBuilder(
                        context = context,
                        klass = SolatDatabase::class.java,
                        name = "solat_database"
                    )
                    .build().also { Instance = it }
            }
        }
    }
}