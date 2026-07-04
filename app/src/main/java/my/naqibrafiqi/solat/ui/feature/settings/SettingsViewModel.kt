package my.naqibrafiqi.solat.ui.feature.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.model.enums.AppLanguage
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.util.states.UiEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent<String>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _zoneCode: Flow<String?> =
        dataStore.getZoneCode()
            .catch { e ->
                e.printStackTrace()
                emit(null)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val zone: StateFlow<Zone?> =
        _zoneCode.flatMapLatest { zoneCode ->
            if (zoneCode == null) {
                return@flatMapLatest flowOf(null)
            }

            solatRepository.fetchZoneByCode(zoneCode)
                .catch { e ->
                    e.printStackTrace()
                    emit(null)
                }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val theme: StateFlow<ThemeSettings> =
        dataStore.getTheme()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ThemeSettings.SYSTEM
            )

    fun updateTheme(theme: ThemeSettings) {
        viewModelScope.launch {
            try {
                dataStore.setTheme(theme)

                _uiEvent.send(
                    UiEvent.Success(
                        data = theme.name,
                        message = "Success: ${theme.name} selected!"
                    )
                )
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Error(errorMessage = "Unexpected error occurred: ${e.message}"))
            }
        }
    }

    fun getLocale(): AppLanguage {
        val currentLocales = AppCompatDelegate.getApplicationLocales()

        return if (!currentLocales.isEmpty) {
            currentLocales[0]?.language?.let { lang ->
                AppLanguage.entries.find { it.langCode == lang }
            } ?: AppLanguage.ENGLISH
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun updateLocale(locale: AppLanguage) {
        val appLocale = LocaleListCompat.forLanguageTags(locale.langCode)

        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}
