package my.naqibrafiqi.solat.data.preference

import android.content.Context
import my.naqibrafiqi.solat.data.preference.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.preference.datastore.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
