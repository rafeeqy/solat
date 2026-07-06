package my.naqibrafiqi.solat.ui.feature.settings.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.domain.usecase.ScheduleNextPrayersUseCase
import my.naqibrafiqi.solat.util.states.Resource
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository,
    private val scheduleNextPrayers: ScheduleNextPrayersUseCase
) : ViewModel() {

    val enabledPrayers: StateFlow<Set<String>> =
        dataStore.getEnabledPrayers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = DataStoreManager.DEFAULT_ENABLED_PRAYERS
            )

    fun onPrayerToggle(prayer: PrayerName, isEnabled: Boolean) {
        viewModelScope.launch {
            dataStore.togglePrayerNotification(prayer.name, isEnabled)

            val updateSet = dataStore.getEnabledPrayers().first()

            val zoneCode = dataStore.getZoneCode().firstOrNull() ?: return@launch

            val zone = solatRepository.fetchZoneByCode(zoneCode).firstOrNull() ?: return@launch

            val prayerResource = solatRepository.fetchDailyPrayersForTodayAndTomorrow(
                zoneId = zone.id,
                zoneCode = zone.code,
                today = LocalDate.now()
            ).firstOrNull { it !is Resource.Loading }

            when (prayerResource) {
                is Resource.Error -> {
                    return@launch
                }

                is Resource.Success -> {
                    if (prayerResource.data.isNotEmpty()) {
                        scheduleNextPrayers(prayerResource.data, updateSet)
                    } else {
                        return@launch
                    }
                }

                else -> {
                    return@launch
                }
            }
        }
    }
}
