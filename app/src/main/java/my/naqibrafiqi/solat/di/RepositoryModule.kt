package my.naqibrafiqi.solat.di

import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.data.remote.WaktuSolatApiService
import my.naqibrafiqi.solat.data.repository.OfflineSolatRepository
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSolatRepository(
        waktuSolatApiService: WaktuSolatApiService,
        solatDao: SolatDao,
        dataStore: SolatDataStore,
        workManager: WorkManager
    ): SolatRepository {
        return OfflineSolatRepository(waktuSolatApiService, solatDao, dataStore, workManager)
    }
}
