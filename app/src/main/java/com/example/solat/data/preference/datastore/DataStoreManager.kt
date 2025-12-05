package com.example.solat.data.preference.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.solat.data.preference.data.Language
import com.example.solat.data.preference.data.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "solat_preferences")

class DataStoreManager @Inject constructor(context: Context) : PreferenceStorage {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val ZONE = intPreferencesKey(name = "zone")
        val THEME = stringPreferencesKey(name = "theme")
        val LOCALE = stringPreferencesKey(name = "locale")
    }

    override fun getCurrentZoneId(): Flow<Int> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.ZONE] ?: 0
        }
    }

    override suspend fun setCurrentZoneId(zoneId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ZONE] = zoneId
        }
    }

    override fun getTheme(): Flow<String> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.THEME] ?: Theme.SYSTEM.name
        }
    }

    override suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    override fun getLocale(): Flow<String> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.LOCALE] ?: Language.ENGLISH.name
        }
    }

    override suspend fun setLocale(locale: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCALE] = locale
        }
    }
}