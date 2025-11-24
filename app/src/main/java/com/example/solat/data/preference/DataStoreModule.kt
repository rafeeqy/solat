package com.example.solat.data.preference

import android.content.Context
import com.example.solat.data.preference.datastore.DataStoreManager
import com.example.solat.data.preference.datastore.PreferenceStorage
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
