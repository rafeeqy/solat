package my.naqibrafiqi.solat.ui.feature.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.PrayerTime
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.domain.usecase.ScheduleNextPrayersUseCase
import my.naqibrafiqi.solat.util.states.Resource
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class DailyPrayerViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository,
    private val scheduleNextPrayers: ScheduleNextPrayersUseCase,
    private val clock: Clock
) : ViewModel() {

    private val _zoneCode: Flow<String?> =
        dataStore.getZoneCode()
            .catch { e ->
                e.printStackTrace()
                emit(null)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val zone: StateFlow<Zone?> =
        _zoneCode.flatMapLatest { zoneCode ->
            if (zoneCode == null) {
                return@flatMapLatest flowOf(null)
            }

            solatRepository.fetchZoneByCode(zoneCode)
                .catch { e ->
                    e.printStackTrace()
                    emit(null)
                }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    // Today's date in Malaysia time. Changing it loads the prayers for the new day.
    private val _dateTrigger = MutableStateFlow(LocalDate.now(clock))

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayAndTomorrowPrayers: StateFlow<List<DailyPrayer>> =
        combine(
            zone,
            _dateTrigger
        ) { z, dt -> z to dt }
            .flatMapLatest { (zone, currentDate) ->
                if (zone == null) {
                    return@flatMapLatest flowOf(emptyList())
                }

                solatRepository.fetchDailyPrayersForTodayAndTomorrow(
                    zone.id,
                    zone.code,
                    currentDate
                )
                    .map { resource ->
                        when (resource) {
                            is Resource.Error -> emptyList()
                            is Resource.Loading -> emptyList()
                            is Resource.Success -> resource.data
                        }
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    private val _countdownText = MutableStateFlow("--:--:--")
    val countdownText = _countdownText.asStateFlow()

    private val _nextPrayer = MutableStateFlow<PrayerTime?>(null)
    val nextPrayer = _nextPrayer.asStateFlow()

    init {
        viewModelScope.launch {
            todayAndTomorrowPrayers.collectLatest { prayerDetails ->
                if (prayerDetails.isEmpty()) {
                    _countdownText.update { "--:--:--" }
                    return@collectLatest
                }

                val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull()
                    ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

                // Schedule prayers notifications
                scheduleNextPrayers(prayerDetails, enabledPrayers)

                runCountdownLoop(dailyPrayers = prayerDetails)
            }
        }
    }

    private suspend fun runCountdownLoop(dailyPrayers: List<DailyPrayer>) {
        while (currentCoroutineContext().isActive) {
            if (refreshIfDateChanged()) {
                return
            }

            val nextTarget = getNextPrayer(dailyPrayers)

            if (nextTarget == null) {
                // No later prayer is loaded (e.g. tomorrow's times couldn't be downloaded).
                // Wait: at midnight the date changes and the prayers are loaded again.
                _nextPrayer.update { null }
                _countdownText.update { "--:--:--" }
                delay(1000.milliseconds)
                continue
            }

            val (prayer, targetTime) = nextTarget

            _nextPrayer.update { prayer }

            startCountdown(targetTime).collect { timeString ->
                _countdownText.update { timeString.toString() }
            }

            delay(1000.milliseconds)
        }
    }

    /**
     * Moves [_dateTrigger] to today if midnight has passed, which loads the new day's prayers and
     * restarts the countdown. Returns true if the date changed.
     */
    private fun refreshIfDateChanged(): Boolean {
        val today = LocalDate.now(clock)

        if (today == _dateTrigger.value) {
            return false
        }

        _dateTrigger.update { today }
        return true
    }

    private fun getNextPrayer(
        dailyPrayers: List<DailyPrayer>
    ): Pair<PrayerTime, LocalDateTime>? {
        val now = LocalDateTime.now(clock)

        return dailyPrayers.flatMap { dailyPrayer ->
            dailyPrayer.prayerTimes
                .filter { it.prayer != PrayerName.IMSAK && it.prayer != PrayerName.SYURUK && it.prayer != PrayerName.DHUHA }
                .map { prayerTime ->
                    val dateTime = LocalDateTime.of(dailyPrayer.date, prayerTime.time)
                    Pair(prayerTime, dateTime)
                }
        }
            .sortedBy { it.second }
            .firstOrNull { it.second.isAfter(now) }
    }

    private fun startCountdown(targetTime: LocalDateTime): Flow<String?> = flow {
        while (true) {
            val now = LocalDateTime.now(clock)

            // The countdown to Subuh runs past midnight: switch to the new day's prayers.
            if (refreshIfDateChanged()) {
                break
            }

            if (!now.isBefore(targetTime)) {
                emit("00:00:00")
                break
            }

            val duration = Duration.between(now, targetTime)
            val totalSeconds = duration.seconds

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            emit(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds))

            delay(1000.milliseconds)
        }
    }
}
