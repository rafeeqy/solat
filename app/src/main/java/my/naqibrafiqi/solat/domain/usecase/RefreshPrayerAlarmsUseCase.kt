package my.naqibrafiqi.solat.domain.usecase

import kotlinx.coroutines.flow.firstOrNull
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.util.states.Resource
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/**
 * Loads today's and tomorrow's prayer times for the saved zone (downloading them if needed) and
 * schedules the alarms for the enabled prayers.
 */
class RefreshPrayerAlarmsUseCase @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository,
    private val scheduleNextPrayers: ScheduleNextPrayersUseCase,
    private val clock: Clock
) {

    sealed interface Result {
        /** Alarms were scheduled for today and tomorrow. */
        data object Scheduled : Result

        /** No zone has been chosen yet, so there's nothing to schedule. */
        data object NoZone : Result

        /** Prayer times couldn't be loaded for both days; worth trying again later. */
        data object Incomplete : Result
    }

    suspend operator fun invoke(): Result {
        val zoneCode = dataStore.getZoneCode().firstOrNull() ?: return Result.NoZone
        val zone = solatRepository.fetchZoneByCode(zoneCode).firstOrNull() ?: return Result.NoZone

        val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull()
            ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

        val resource = solatRepository.fetchDailyPrayersForTodayAndTomorrow(
            zoneId = zone.id,
            zoneCode = zone.code,
            today = LocalDate.now(clock)
        ).firstOrNull { it is Resource.Success }

        val dailyPrayers = (resource as? Resource.Success)?.data.orEmpty()

        // Schedule whatever was loaded, even if one day is missing: it's better than nothing.
        // Disabled prayers are cancelled either way.
        scheduleNextPrayers(dailyPrayers, enabledPrayers)

        return if (dailyPrayers.size >= 2) Result.Scheduled else Result.Incomplete
    }
}
