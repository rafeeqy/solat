package com.example.solat.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solat.alarm.AlarmSchedulerRepository
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.derived.DailyPrayerDetails
import com.example.solat.data.preference.datastore.PreferenceStorage
import com.example.solat.data.repository.SolatRepository
import com.example.solat.util.SolatDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimeViewModel @Inject constructor(
    private val dataStore: PreferenceStorage,
    private val solatRepository: SolatRepository,
    private val alarmSchedulerRepository: AlarmSchedulerRepository
) : ViewModel() {

    var date: Long by mutableLongStateOf(SolatDateTime.nowEpochMilli())

    private val _previousIsyak: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val _zoneId: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _zone: MutableStateFlow<Zone?> = MutableStateFlow(null)
    var zone: StateFlow<Zone?> = _zone.asStateFlow()

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
            dataStore.getCurrentZoneId().collect { id ->
                _zoneId.value = id

                solatRepository.fetchZoneById(_zoneId.value).collect {
                    _zone.value = it

                    refresh()
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            val currTimeMillis = System.currentTimeMillis()

            if (_zoneId.value > 0 && _zone.value != null) {
                solatRepository.fetchDailyPrayers(_zone.value!!).catch { e ->
                    _uiState.value = PrayerTimeUiState.Error(
                        message = e.message ?: "Error: Failed to fetch prayer times"
                    )
                }.collect { dailyPrayer ->
                    if (dailyPrayer.isNotEmpty()) {
                        _waktuSolat.value = parsePrayerTimes(dailyPrayer)

                        _upcomingPrayer.value = parseNextPrayer()

                        _countdown.value =
                            (_upcomingPrayer.value?.time ?: currTimeMillis) - currTimeMillis

                        // TODO: Need to be refactored...
                        // alarmSchedulerRepository.schedule(_upcomingPrayer.value!!)
                    }

                    _uiState.value = PrayerTimeUiState.Success
                }
            }
        }
    }

    private fun parsePrayerTimes(dailyPrayer: List<DailyPrayerDetails>): List<WaktuSolat> {
        val prayers = mutableListOf<WaktuSolat>()

        dailyPrayer.forEach { prayer ->
            prayers.addAll(
                listOf(
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = "Subuh",
                        time = (prayer.fajr.toString() + "000").toLong()
                    ),
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = "Zohor",
                        time = (prayer.dhuhr.toString() + "000").toLong()
                    ),
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = "Asar",
                        time = (prayer.asr.toString() + "000").toLong()
                    ),
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = "Maghrib",
                        time = (prayer.maghrib.toString() + "000").toLong()
                    ),
                    WaktuSolat(
                        year = prayer.year,
                        month = prayer.month,
                        day = prayer.day,
                        name = "Isyak",
                        time = (prayer.isha.toString() + "000").toLong()
                    )
                )
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
