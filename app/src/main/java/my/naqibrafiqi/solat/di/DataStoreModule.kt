package my.naqibrafiqi.solat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.PreferenceStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): PreferenceStorage {
        return DataStoreManager(context)
    }
}