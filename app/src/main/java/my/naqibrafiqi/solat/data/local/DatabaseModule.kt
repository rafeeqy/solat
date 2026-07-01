package my.naqibrafiqi.solat.data.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.data.local.dao.SolatDao
import my.naqibrafiqi.solat.data.local.db.SolatDatabase
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
