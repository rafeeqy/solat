package com.example.solat

import com.example.solat.data.local.dao.SolatDao
import com.example.solat.data.remote.service.WaktuSolatApiService
import com.example.solat.data.repository.OfflineSolatRepository
import com.example.solat.data.repository.SolatRepository
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

    // TODO: Add ViewModel provider here...
}
