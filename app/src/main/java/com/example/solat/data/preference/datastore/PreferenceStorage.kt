package com.example.solat.data.preference.datastore

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    fun getCurrentZoneId(): Flow<Int>

    suspend fun setCurrentZoneId(zoneId: Int)

    fun getTheme(): Flow<String>

    suspend fun setTheme(theme: String)

    fun getLocale(): Flow<String>

    suspend fun setLocale(locale: String)
}
