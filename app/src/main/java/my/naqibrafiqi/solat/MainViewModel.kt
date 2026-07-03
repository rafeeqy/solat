package my.naqibrafiqi.solat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingGraph
import my.naqibrafiqi.solat.ui.navigation.route.OnboardingRoutes
import my.naqibrafiqi.solat.ui.navigation.route.PrayerGraph
import my.naqibrafiqi.solat.ui.navigation.route.PrayerRoutes
import my.naqibrafiqi.solat.ui.navigation.route.SolatRoutes
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: SolatDataStore
) : ViewModel() {

    var isLoading = mutableStateOf(false)
        private set

    var startDestination = mutableStateOf<SolatRoutes>(OnboardingGraph)
        private set

    val theme: StateFlow<ThemeSettings> =
        dataStore.getTheme()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ThemeSettings.SYSTEM
            )

    init {
        viewModelScope.launch {
            isLoading.value = true

            val zoneCode = dataStore.getZoneCode().firstOrNull()

            startDestination.value = if (zoneCode == null) {
                OnboardingGraph
            } else {
                PrayerGraph
            }

            isLoading.value = false
        }
    }
}
