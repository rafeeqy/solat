package my.naqibrafiqi.solat.di

import androidx.work.WorkManager
import my.naqibrafiqi.solat.jobs.alarm.AlarmSchedulerRepository
import my.naqibrafiqi.solat.data.local.dao.SolatDao
import my.naqibrafiqi.solat.data.preference.datastore.PreferenceStorage
import my.naqibrafiqi.solat.data.remote.service.WaktuSolatApiService
import my.naqibrafiqi.solat.data.repository.OfflineSolatRepository
import my.naqibrafiqi.solat.data.repository.SolatRepository
import my.naqibrafiqi.solat.ui.viewmodel.OnboardingViewModel
import my.naqibrafiqi.solat.ui.viewmodel.PrayerTimeViewModel
import my.naqibrafiqi.solat.ui.viewmodel.ZoneLocationViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SolatApplicationModule {
    @Singleton
    @Provides
    fun provideSolatRepository(
        waktuSolatApiService: WaktuSolatApiService,
        solatDao: SolatDao,
        dataStore: PreferenceStorage,
        workManager: WorkManager
    ): SolatRepository {
        return OfflineSolatRepository(waktuSolatApiService, solatDao, dataStore, workManager)
    }

    @Provides
    fun provideOnboardingViewModel(
        dataStore: PreferenceStorage,
        solatRepository: SolatRepository
    ) = OnboardingViewModel(dataStore, solatRepository)

    @Provides
    fun provideZoneLocationViewModel(
        dataStore: PreferenceStorage,
        solatRepository: SolatRepository
    ) = ZoneLocationViewModel(dataStore, solatRepository)

    @Provides
    fun providePrayerTimeViewModel(
        dataStore: PreferenceStorage,
        solatRepository: SolatRepository,
        alarmSchedulerRepository: AlarmSchedulerRepository
    ) = PrayerTimeViewModel(dataStore, solatRepository, alarmSchedulerRepository)
}
