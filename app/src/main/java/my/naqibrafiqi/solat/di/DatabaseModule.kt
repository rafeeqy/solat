package my.naqibrafiqi.solat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.data.local.db.SolatDatabase
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SolatDatabase {
        return SolatDatabase.getDatabase(context)
    }

    @Provides
    fun provideSolatDao(database: SolatDatabase): SolatDao {
        return database.solatDao()
    }
}