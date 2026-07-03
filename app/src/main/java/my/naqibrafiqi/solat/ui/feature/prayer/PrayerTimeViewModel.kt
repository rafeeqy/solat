package my.naqibrafiqi.solat.ui.feature.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.jobs.alarm.AlarmSchedulerRepository
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.repository.Prayer
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import javax.inject.Inject

@HiltViewModel
class PrayerTimeViewModel @Inject constructor(
    private val dataStore: SolatDataStore,
    private val solatRepository: SolatRepository,
    private val alarmSchedulerRepository: AlarmSchedulerRepository
) : ViewModel() {

    private val _zoneId: MutableStateFlow<Int> = MutableStateFlow(1)

    private val _zoneEntity: MutableStateFlow<ZoneEntity?> = MutableStateFlow(null)
    var zoneEntity: StateFlow<ZoneEntity?> = _zoneEntity.asStateFlow()

    private val _waktuSolat: MutableStateFlow<List<WaktuSolat>> = MutableStateFlow(emptyList())
    val waktuSolat: StateFlow<List<WaktuSolat>> = _waktuSolat.asStateFlow()

    private val _upcomingPrayer: MutableStateFlow<WaktuSolat?> = MutableStateFlow(null)
    val upcomingPrayer: StateFlow<WaktuSolat?> = _upcomingPrayer.asStateFlow()

    private val _countdown: MutableStateFlow<Long> = MutableStateFlow(0L)
    val countdown: StateFlow<Long> = _countdown.asStateFlow()

    private val _uiState: MutableStateFlow<PrayerTimeUiState> =
        MutableStateFlow(PrayerTimeUiState.Loading)
    val uiState: StateFlow<PrayerTimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            solatRepository.fetchZoneById(_zoneId.value).collect {
                _zoneEntity.value = it

                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            val currTimeMillis = System.currentTimeMillis()

            if (_zoneId.value > 0 && _zoneEntity.value != null) {
                solatRepository.fetchDailyPrayers(_zoneEntity.value!!).catch { e ->
                    _uiState.value = PrayerTimeUiState.Error(
                        message = e.message ?: "Error: Failed to fetch prayer times"
                    )
                }.collect { dailyPrayer ->
                    if (dailyPrayer.isNotEmpty()) {
                        _waktuSolat.value = parsePrayerTimes(dailyPrayer)

                        _upcomingPrayer.value = parseNextPrayer()

                        _countdown.value =
                            (_upcomingPrayer.value?.time ?: currTimeMillis) - currTimeMillis

                        alarmSchedulerRepository.schedule(
                            _zoneId.value,
                            _zoneEntity.value!!.code,
                            _upcomingPrayer.value?.time ?: currTimeMillis
                        )
                    }

                    _uiState.value = PrayerTimeUiState.Success
                }
            }
        }
    }

    private fun parsePrayerTimes(dailyPrayer: List<Prayer>): List<WaktuSolat> {
        val prayers = mutableListOf<WaktuSolat>()

        dailyPrayer.forEach { prayer ->
            prayers.addAll(
                prayer.prayers.map {
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = it.key.label,
                        time = it.value
                    )
                }
            )
        }

        return prayers
    }

    private fun parseNextPrayer(): WaktuSolat? {
        val prayers = _waktuSolat.value
        val currentTime = System.currentTimeMillis()

        return if (prayers.isNotEmpty()) {
            prayers.find { it.time > currentTime }
        } else {
            null
        }
    }

    fun calculateTimeLeft() {
        viewModelScope.launch {
            val upcomingPrayer = _upcomingPrayer.value

            if (upcomingPrayer != null) {
                while (_countdown.value > 0) {
                    delay(1000)

                    _countdown.value = upcomingPrayer.time - System.currentTimeMillis()
                }

                _upcomingPrayer.value = parseNextPrayer()
                _countdown.value = upcomingPrayer.time - System.currentTimeMillis()
            }
        }
    }
}

sealed interface PrayerTimeUiState {
    data object Loading : PrayerTimeUiState

    data class Error(
        val message: String
    ) : PrayerTimeUiState

    data object Success : PrayerTimeUiState
}


data class WaktuSolat(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val name: String = "",
    val time: Long = 0L
)
