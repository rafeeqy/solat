package my.naqibrafiqi.solat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.domain.usecase.ScheduleNextPrayersUseCase
import my.naqibrafiqi.solat.util.states.Resource
import java.time.LocalDate

@HiltWorker
class DailyAlarmWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository,
    private val scheduleNextPrayers: ScheduleNextPrayersUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val zoneCode = dataStore.getZoneCode().firstOrNull() ?: return Result.failure()

            val zone =
                solatRepository.fetchZoneByCode(zoneCode).firstOrNull() ?: return Result.failure()

            val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull() ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

            val prayerResource = solatRepository.fetchDailyPrayersForTodayAndTomorrow(
                zoneId = zone.id,
                zoneCode = zone.code,
                today = LocalDate.now()
            ).firstOrNull { it !is Resource.Loading }

            when (prayerResource) {
                is Resource.Error -> {
                    Result.retry()
                }

                is Resource.Success -> {
                    if (prayerResource.data.isNotEmpty()) {
                        scheduleNextPrayers(prayerResource.data, enabledPrayers)
                        Result.success()
                    } else {
                        Result.retry()
                    }
                }

                else -> {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
