package my.naqibrafiqi.solat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.domain.usecase.ScheduleNextPrayersUseCase
import my.naqibrafiqi.solat.util.states.Resource
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataStore: SolatDataStore

    @Inject
    lateinit var solatRepository: SolatRepository

    @Inject
    lateinit var scheduleNextPrayers: ScheduleNextPrayersUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            return
        }

        scope.launch {
            val zoneCode = dataStore.getZoneCode().firstOrNull() ?: return@launch

            val zone =
                solatRepository.fetchZoneByCode(zoneCode).firstOrNull() ?: return@launch

            val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull() ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

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
                        scheduleNextPrayers(prayerResource.data, enabledPrayers)
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
