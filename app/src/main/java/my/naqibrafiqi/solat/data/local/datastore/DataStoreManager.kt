package my.naqibrafiqi.solat.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SolatDataStore {

    companion object {
        val THEME_KEY = stringPreferencesKey(name = "theme")
        val ZONE_CODE_KEY = stringPreferencesKey(name = "zone_code")
        val ENABLED_PRAYERS_KEY = stringSetPreferencesKey("enabled_prayers")
        val DEFAULT_ENABLED_PRAYERS = setOf(
            PrayerName.SUBUH.name,
            PrayerName.ZOHOR.name,
            PrayerName.ASAR.name,
            PrayerName.MAGHRIB.name,
            PrayerName.ISYAK.name
        )
    }

    override fun getTheme(): Flow<ThemeSettings> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            val themeName = preferences[THEME_KEY] ?: ThemeSettings.SYSTEM.name

            runCatching { ThemeSettings.valueOf(themeName) }
                .getOrDefault(ThemeSettings.SYSTEM)
        }
    }

    override suspend fun setTheme(theme: ThemeSettings) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
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
            preferences[ZONE_CODE_KEY]
        }
    }

    override suspend fun setZoneCode(code: String) {
        dataStore.edit { preferences ->
            preferences[ZONE_CODE_KEY] = code
        }
    }

    override fun getEnabledPrayers(): Flow<Set<String>> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[ENABLED_PRAYERS_KEY] ?: DEFAULT_ENABLED_PRAYERS
        }
    }

    override suspend fun togglePrayerNotification(
        prayerName: String,
        isEnabled: Boolean
    ) {
        dataStore.edit { preferences ->
            val currentSet = preferences[ENABLED_PRAYERS_KEY] ?: DEFAULT_ENABLED_PRAYERS
            val mutableSet = currentSet.toMutableSet()

            if (isEnabled) {
                mutableSet.add(prayerName)
            } else {
                mutableSet.remove(prayerName)
            }

            preferences[ENABLED_PRAYERS_KEY] = mutableSet
        }
    }
}