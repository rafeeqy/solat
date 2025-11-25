package com.example.solat.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.preference.datastore.PreferenceStorage
import com.example.solat.data.repository.SolatRepository
import com.example.solat.util.CatchError
import com.example.solat.util.SolatDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZoneLocationViewModel @Inject constructor(
    private val dataStore: PreferenceStorage,
    private val solatRepository: SolatRepository
) : ViewModel() {
    var currentZoneId: Int by mutableIntStateOf(0)

    private val _uiState: MutableStateFlow<ZoneLocationUiState> =
        MutableStateFlow(ZoneLocationUiState.Loading)
    val uiState: StateFlow<ZoneLocationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = ZoneLocationUiState.Loading

            fetchZonePreference()
            fetchZones()
        }
    }

    fun fetchZonePreference() {
        viewModelScope.launch(context = Dispatchers.IO) {
            dataStore.getCurrentZoneId()
                .collect { zoneId ->
                    currentZoneId = zoneId
                }
        }
    }

    fun fetchZones() {
        viewModelScope.launch(context = Dispatchers.IO) {
            solatRepository.fetchAllZones()
                .catch { e ->
                    _uiState.value =
                        ZoneLocationUiState.Error(message = e.message ?: "Error 404: Not Found")
                }
                .collect { zones -> _uiState.value = ZoneLocationUiState.Success(zones) }
        }
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = ZoneLocationUiState.Loading

            solatRepository.remoteFetchAllZones()
        }
    }

    suspend fun selectZone(zone: Zone): CatchError {
        return try {
            dataStore.setCurrentZoneId(zone.zoneId)

            val currentDate = SolatDateTime.formatDate(
                date = SolatDateTime.nowEpochMilli(),
                format = "uuuu-MM-dd"
            ).split("-")

            solatRepository.remoteFetchPrayerTimes(
                zone = zone,
                year = currentDate[0].toInt(),
                month = currentDate[1].toInt()
            )

            CatchError(
                isError = false,
                message = "Success: Zone: ${zone.code} selected!"
            )
        } catch (e: Exception) {
            CatchError(
                isError = true,
                message = e.message ?: "Error: Failed to select zone"
            )
        }
    }
}

sealed interface ZoneLocationUiState {
    data object Loading : ZoneLocationUiState

    data class Error(
        val message: String
    ) : ZoneLocationUiState

    data class Success(
        val zones: List<Zone> = emptyList()
    ) : ZoneLocationUiState
}
