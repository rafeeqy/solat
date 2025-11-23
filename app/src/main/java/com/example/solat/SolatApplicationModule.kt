package com.example.solat

import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.data.repository.OfflineSolatRepository
import com.example.solat.data.repository.SolatRepository
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

    fun provideZoneLocationViewModel(solatRepository: SolatRepository) =
        ZoneLocationViewModel(solatRepository)
}
