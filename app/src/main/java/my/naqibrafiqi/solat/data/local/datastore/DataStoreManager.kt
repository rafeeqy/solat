package my.naqibrafiqi.solat.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import my.naqibrafiqi.solat.domain.model.enums.AppLanguage
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SolatDataStore {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey(name = "theme")
        val ZONE_CODE = stringPreferencesKey(name = "zone_code")
        val LOCALE = stringPreferencesKey(name = "locale") // TODO: To be removed
        val ISYAK = longPreferencesKey(name = "isyak") // TODO: To be removed
    }

    override fun getTheme(): Flow<ThemeSettings> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: ThemeSettings.SYSTEM.name

            runCatching { ThemeSettings.valueOf(themeName) }
                .getOrDefault(ThemeSettings.SYSTEM)
        }
    }

    override suspend fun setTheme(theme: ThemeSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    override fun getZoneCode(): Flow<String?> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.ZONE_CODE]
        }
    }

    override suspend fun setZoneCode(code: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ZONE_CODE] = code
        }
    }

    // TODO: To be removed
    override fun getLocale(): Flow<String> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.LOCALE] ?: AppLanguage.ENGLISH.name
        }
    }

    // TODO: To be removed
    override suspend fun setLocale(locale: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCALE] = locale
        }
    }

    // TODO: To be removed
    override fun getPreviousIsyakPrayer(): Flow<Long?> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[PreferencesKeys.ISYAK]
        }
    }

    // TODO: To be removed
    override suspend fun setPreviousIsyakPrayer(isyakPrayer: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ISYAK] = isyakPrayer
        }
    }
}