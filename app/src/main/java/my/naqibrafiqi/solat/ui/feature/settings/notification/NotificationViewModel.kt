package my.naqibrafiqi.solat.ui.feature.settings.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.usecase.RefreshPrayerAlarmsUseCase
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val refreshPrayerAlarms: RefreshPrayerAlarmsUseCase
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

            try {
                refreshPrayerAlarms()
            } catch (e: Exception) {
                // The alarm receiver also checks the setting, so a switched-off prayer won't
                // notify even if its alarm couldn't be cancelled here.
                Log.w("NotificationViewModel", "Couldn't refresh the alarms", e)
            }
        }
    }
}
