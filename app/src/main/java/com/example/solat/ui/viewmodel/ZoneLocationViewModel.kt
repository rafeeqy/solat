package com.example.solat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.preference.datastore.PreferenceStorage
import com.example.solat.data.repository.SolatRepository
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
    private val _uiState: MutableStateFlow<ZoneLocationUiState> =
        MutableStateFlow(ZoneLocationUiState.Loading)
    val uiState: StateFlow<ZoneLocationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = ZoneLocationUiState.Loading

            fetchZones()
        }
    }

    suspend fun fetchZones() {
        solatRepository.fetchAllZones()
            .catch { e ->
                _uiState.value =
                    ZoneLocationUiState.Error(message = e.message ?: "Error 404: Not Found")
            }
            .collect { zones -> _uiState.value = ZoneLocationUiState.Success(zones) }
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = ZoneLocationUiState.Loading

            solatRepository.remoteFetchAllZones()
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
