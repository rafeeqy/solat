package com.example.solat.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solat.alarm.AlarmSchedulerRepository
import com.example.solat.data.local.entity.Zone
import com.example.solat.data.local.entity.relationship.PrayerTimeAndDetails
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

    private val _waktuSolat: MutableStateFlow<WaktuSolat?> = MutableStateFlow(null)
    val waktuSolat: StateFlow<WaktuSolat?> = _waktuSolat.asStateFlow()

    private val _upcomingPrayer: MutableStateFlow<Prayer?> = MutableStateFlow(null)
    val upcomingPrayer: StateFlow<Prayer?> = _upcomingPrayer.asStateFlow()

    private val _countdown: MutableStateFlow<Long> = MutableStateFlow(0L)
    val countdown: StateFlow<Long> = _countdown.asStateFlow()

    private val _uiState: MutableStateFlow<PrayerTimeUiState> =
        MutableStateFlow(PrayerTimeUiState.Loading)
    val uiState: StateFlow<PrayerTimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            dataStore.getCurrentZoneId().collect { id ->
                _zoneId.value = id
                getZone()

                dataStore.getPreviousIsyakPrayer().collect { isyak ->
                    _previousIsyak.value = isyak
                    refresh()
                }
            }
        }
    }

    private fun getZone() {
        viewModelScope.launch(context = Dispatchers.IO) {
            solatRepository.fetchZoneById(_zoneId.value).collect { _zone.value = it }
        }
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            date = if (_previousIsyak.value != null && date > _previousIsyak.value!!) {
                SolatDateTime.tomorrowEpochMilli()
            } else {
                SolatDateTime.nowEpochMilli()
            }

            val splitDate = SolatDateTime.formatDate(
                date = date,
                format = "uuuu-MM-dd"
            ).split("-")

            if (_zoneId.value > 0) {
                solatRepository.fetchPrayer(
                    zoneId = _zoneId.value,
                    year = splitDate[0].toInt(),
                    month = splitDate[1].toInt(),
                    day = splitDate[2].toInt()
                ).catch { e ->
                    _uiState.value = PrayerTimeUiState.Error(
                        message = e.message ?: "Error: Failed to fetch prayer times"
                    )
                }.collect { dailyPrayer ->
                    if (dailyPrayer != null) {
                        _waktuSolat.value = parsePrayerTimes(dailyPrayer)

                        _upcomingPrayer.value = parseNextPrayer()

                        _countdown.value = _upcomingPrayer.value!!.time - System.currentTimeMillis()

                        alarmSchedulerRepository.schedule(_upcomingPrayer.value!!)
                    }

                    _uiState.value = PrayerTimeUiState.Success
                }
            }
        }
    }

    private fun parsePrayerTimes(dailyPrayer: PrayerTimeAndDetails): WaktuSolat {
        val prayers = listOf(
            Prayer(
                name = "Subuh",
                time = (dailyPrayer.time.fajr.toString() + "000").toLong()
            ),
            Prayer(
                name = "Zohor",
                time = (dailyPrayer.time.dhuhr.toString() + "000").toLong()
            ),
            Prayer(
                name = "Asar",
                time = (dailyPrayer.time.asr.toString() + "000").toLong()
            ),
            Prayer(
                name = "Maghrib",
                time = (dailyPrayer.time.maghrib.toString() + "000").toLong()
            ),
            Prayer(
                name = "Isyak",
                time = (dailyPrayer.time.isha.toString() + "000").toLong()
            )
        )

        return WaktuSolat(
            year = dailyPrayer.details.year,
            month = dailyPrayer.details.monthNumber,
            day = dailyPrayer.time.day,
            hijri = dailyPrayer.time.hijri,
            prayers = prayers
        )
    }

    private fun parseNextPrayer(): Prayer? {
        return _waktuSolat.value?.prayers?.find { prayer ->
            val currentTime = System.currentTimeMillis()

            prayer.time > currentTime
        }
    }

    fun calculateTimeLeft() {
        viewModelScope.launch {
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value = _upcomingPrayer.value!!.time - System.currentTimeMillis()
            }

            if (_upcomingPrayer.value!!.name == "Isyak") {
                date = SolatDateTime.tomorrowEpochMilli()
                dataStore.setPreviousIsyakPrayer(_upcomingPrayer.value!!.time)
                refresh()
            } else {
                _upcomingPrayer.value = parseNextPrayer()
                _countdown.value = _upcomingPrayer.value!!.time - System.currentTimeMillis()
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
    val hijri: String = "",
    val prayers: List<Prayer> = emptyList()
)

data class Prayer(
    val name: String = "",
    val time: Long = 0L
)
