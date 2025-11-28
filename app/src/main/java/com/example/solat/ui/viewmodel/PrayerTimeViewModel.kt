package com.example.solat.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solat.data.local.entity.PrayerTime
import com.example.solat.data.local.entity.Zone
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
    private val solatRepository: SolatRepository
) : ViewModel() {
    var prayerDate: Long by mutableLongStateOf(SolatDateTime.nowEpochMilli())

    private val _currentZone: MutableStateFlow<Zone?> =
        MutableStateFlow(null)
    val currentZone: StateFlow<Zone?> = _currentZone.asStateFlow()

    private val _prayerTimes: MutableStateFlow<List<PrayerDateTime>> =
        MutableStateFlow(emptyList())
    val prayerTimes: StateFlow<List<PrayerDateTime>> = _prayerTimes.asStateFlow()

    var nextPrayer: PrayerDateTime by mutableStateOf(PrayerDateTime())

    private val _uiState: MutableStateFlow<PrayerTimeUiState> =
        MutableStateFlow(PrayerTimeUiState.Loading)
    val uiState: StateFlow<PrayerTimeUiState> = _uiState.asStateFlow()

    private val _countdown: MutableStateFlow<Long> = MutableStateFlow(0L)
    val countdown: StateFlow<Long> = _countdown.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            val currentDate = SolatDateTime.formatDate(
                date = prayerDate,
                format = "uuuu-MM-dd"
            ).split("-")

            dataStore.getCurrentZoneId()
                .collect { zoneId ->
                    if (zoneId > 0) {
                        fetchPrayerTimes(zoneId, currentDate)
                    }
                }
        }
    }

    private suspend fun fetchPrayerTimes(
        zoneId: Int,
        currentDate: List<String>
    ) {
        solatRepository.fetchPrayerTimes(
            zoneId = zoneId,
            year = currentDate[0].toInt(),
            month = currentDate[1].toInt(),
            day = currentDate[2].toInt()
        ).catch { e ->
            _uiState.value = PrayerTimeUiState.Error(
                message = e.message ?: "Error: Failed to fetch prayer times"
            )
        }.collect { todayPrayer ->
            if (todayPrayer != null) {
                _currentZone.value = todayPrayer.prayerDetails.zone

                _prayerTimes.value = parsePrayerTimes(todayPrayer.time)

                nextPrayer = parseNextPrayer()

                _countdown.value = nextPrayer.time - System.currentTimeMillis()
            }

            _uiState.value = PrayerTimeUiState.Success
        }
    }

    private fun parsePrayerTimes(prayer: PrayerTime): List<PrayerDateTime> {
        return listOf(
            PrayerDateTime(
                day = prayer.day,
                hijri = prayer.hijri,
                prayer = "Subuh",
                time = (prayer.fajr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayer.day,
                hijri = prayer.hijri,
                prayer = "Zohor",
                time = (prayer.dhuhr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayer.day,
                hijri = prayer.hijri,
                prayer = "Asar",
                time = (prayer.asr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayer.day,
                hijri = prayer.hijri,
                prayer = "Maghrib",
                time = (prayer.maghrib.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayer.day,
                hijri = prayer.hijri,
                prayer = "Isyak",
                time = (prayer.isha.toString() + "000").toLong()
            )
        )
    }

    private fun parseNextPrayer(): PrayerDateTime {
        return _prayerTimes.value.find { prayer -> prayer.time > SolatDateTime.nowEpochMilli() }!!
    }

    fun calculateTimeLeft() {
        viewModelScope.launch {
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value = nextPrayer.time - System.currentTimeMillis()
            }

            if (nextPrayer.prayer == "Isyak") {
                prayerDate = SolatDateTime.tomorrowEpochMilli()
                refresh()
            } else {
                nextPrayer = parseNextPrayer()
                _countdown.value = nextPrayer.time - System.currentTimeMillis()
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

data class PrayerDateTime(
    val day: Int = 0,
    val hijri: String = "",
    val prayer: String = "",
    val time: Long = 0L
)
