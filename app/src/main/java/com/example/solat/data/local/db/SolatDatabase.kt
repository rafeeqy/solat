package com.example.solat.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.local.entity.PrayerDetails
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone

@Database(
    entities = [
        Zone::class,
        PrayerDetails::class,
        PrayerTime::class
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
                Room.databaseBuilder(
                    context = context,
                    klass = SolatDatabase::class.java,
                    name = "solat_database"
                ).build().also { Instance = it }
            }
        }
    }
}
