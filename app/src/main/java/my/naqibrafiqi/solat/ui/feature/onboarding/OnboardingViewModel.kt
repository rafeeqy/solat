package my.naqibrafiqi.solat.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository
) : ViewModel() {

}