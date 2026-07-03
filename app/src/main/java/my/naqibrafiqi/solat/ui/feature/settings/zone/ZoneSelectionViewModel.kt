package my.naqibrafiqi.solat.ui.feature.settings.zone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.util.states.Resource
import my.naqibrafiqi.solat.util.states.UiEvent
import javax.inject.Inject

@HiltViewModel
class ZoneSelectionViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository
) : ViewModel() {

    val currentZoneCode: StateFlow<String?> =
        dataStore.getZoneCode()
            .catch { e ->
                e.printStackTrace()
                emit(null)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val uiState: StateFlow<ZoneSelectionUiState> =
        solatRepository.fetchAllZones()
            .scan(ZoneSelectionUiState(isLoading = true)) { currentState, resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (currentState.zones.isEmpty()) {
                            currentState.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        } else {
                            currentState.copy(
                                isInternalLoading = true,
                                isInternalError = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        if (currentState.zones.isEmpty()) {
                            currentState.copy(
                                isLoading = false,
                                errorMessage = "Unexpected error occurred: ${resource.exception.message}"
                            )
                        } else {
                            currentState.copy(
                                isInternalLoading = false,
                                isInternalError = true
                            )
                        }
                    }

                    is Resource.Success -> {
                        currentState.copy(
                            zones = resource.data,
                            isLoading = false,
                            errorMessage = null,
                            isInternalLoading = false,
                            isInternalError = false
                        )
                    }
                }
            }
            .catch { e ->
                emit(
                    ZoneSelectionUiState(
                        isLoading = false,
                        errorMessage = "Flow crashed: ${e.message}",
                        isInternalError = true
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ZoneSelectionUiState(isLoading = true)
            )

    private val _uiEvent = Channel<UiEvent<String>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun selectZone(zone: Zone) {
        viewModelScope.launch {
            try {
                dataStore.setZoneCode(zone.code)

                _uiEvent.send(
                    UiEvent.Success(
                        data = zone.code,
                        message = "Success: ${zone.state} (${zone.code}) selected!"
                    )
                )
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Error("Unexpected error occurred: ${e.message}"))
            }
        }
    }
}
