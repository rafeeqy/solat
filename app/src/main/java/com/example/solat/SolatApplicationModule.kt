package com.example.solat

import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.preference.datastore.PreferenceStorage
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.data.repository.OfflineSolatRepository
import com.example.solat.data.repository.SolatRepository
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
        solatDao: SolatDao
    ): SolatRepository {
        return OfflineSolatRepository(waktuSolatApiService, solatDao)
    }

    @Provides
    fun provideZoneLocationViewModel(
        dataStore: PreferenceStorage,
        solatRepository: SolatRepository
    ) = ZoneLocationViewModel(dataStore, solatRepository)

    @Provides
    fun providePrayerTimeViewModel(
        dataStore: PreferenceStorage,
        solatRepository: SolatRepository
    ) = PrayerTimeViewModel(dataStore, solatRepository)
}
