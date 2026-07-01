package my.naqibrafiqi.solat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import my.naqibrafiqi.solat.data.local.entity.Zone
import my.naqibrafiqi.solat.data.preference.datastore.PreferenceStorage
import my.naqibrafiqi.solat.data.repository.SolatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: PreferenceStorage,
    private val solatRepository: SolatRepository
) : ViewModel() {
    val currentZoneId: StateFlow<Int> = dataStore.getCurrentZoneId()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _selectedZone: MutableStateFlow<Zone?> = MutableStateFlow(null)
    val selectedZone: StateFlow<Zone?> = _selectedZone.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (currentZoneId.value > 0) {
                solatRepository.fetchZoneById(currentZoneId.value)
                    .catch { _selectedZone.value = null }
                    .collect { _selectedZone.value = it }
            }
        }
    }
}
