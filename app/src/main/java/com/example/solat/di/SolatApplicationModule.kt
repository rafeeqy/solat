package com.example.solat.di

import androidx.work.WorkManager
import com.example.solat.alarm.AlarmSchedulerRepository
import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.preference.datastore.PreferenceStorage
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.data.repository.OfflineSolatRepository
import com.example.solat.data.repository.SolatRepository
import com.example.solat.ui.viewmodel.OnboardingViewModel
import com.example.solat.ui.viewmodel.PrayerTimeViewModel
import com.example.solat.ui.viewmodel.ZoneLocationViewModel
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
        workManager: WorkManager
    ): SolatRepository {
        return OfflineSolatRepository(waktuSolatApiService, solatDao, workManager)
    }

    @Provides
    fun provideOnboardingViewModel(dataStore: PreferenceStorage) = OnboardingViewModel(dataStore)

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
