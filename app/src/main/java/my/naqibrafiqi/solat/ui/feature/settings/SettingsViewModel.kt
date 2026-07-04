package my.naqibrafiqi.solat.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository
) : ViewModel() {

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
}
