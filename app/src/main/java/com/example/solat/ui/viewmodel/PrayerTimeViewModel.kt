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
    var currentZone: Zone? by mutableStateOf(null)

    var prayerDate: Long by mutableLongStateOf(SolatDateTime.nowEpochMilli())

    private val _uiState: MutableStateFlow<PrayerTimeUiState> =
        MutableStateFlow(PrayerTimeUiState.Loading)
    val uiState: StateFlow<PrayerTimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            fetchZone()
            fetchPrayerTimes()
        }
    }

    fun fetchZone() {
        viewModelScope.launch(context = Dispatchers.IO) {
            dataStore.getCurrentZoneId()
                .collect { zoneId ->
                    if (zoneId > 0) {
                        currentZone = solatRepository.fetchZoneById(zoneId)
                    }
                }
        }
    }

    fun fetchPrayerTimes() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val currentDate = SolatDateTime.formatDate(
                date = prayerDate,
                format = "uuuu-MM-dd"
            ).split("-")

            solatRepository.fetchPrayerTimes(
                year = currentDate[0].toInt(),
                month = currentDate[1].toInt(),
                day = currentDate[2].toInt()
            ).catch { e ->
                _uiState.value = PrayerTimeUiState.Error(
                    message = e.message ?: "Error: Failed to fetch prayer times"
                )
            }.collect { prayerTime ->
                _uiState.value = PrayerTimeUiState.Success(parsePrayerTimes(prayerTime))
            }
        }
    }

    private fun parsePrayerTimes(prayerTime: PrayerTime): List<PrayerDateTime> {
        return listOf(
            PrayerDateTime(
                day = prayerTime.day,
                hijri = prayerTime.hijri,
                prayer = "Subuh",
                time = (prayerTime.fajr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayerTime.day,
                hijri = prayerTime.hijri,
                prayer = "Zohor",
                time = (prayerTime.dhuhr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayerTime.day,
                hijri = prayerTime.hijri,
                prayer = "Asar",
                time = (prayerTime.asr.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayerTime.day,
                hijri = prayerTime.hijri,
                prayer = "Maghrib",
                time = (prayerTime.maghrib.toString() + "000").toLong()
            ),
            PrayerDateTime(
                day = prayerTime.day,
                hijri = prayerTime.hijri,
                prayer = "Isyak",
                time = (prayerTime.isha.toString() + "000").toLong()
            ),
        )
    }

    fun refetchPrayerTime() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _uiState.value = PrayerTimeUiState.Loading

            val currentDate = SolatDateTime.formatDate(
                date = prayerDate,
                format = "uuuu-MM-dd"
            ).split("-")

            currentZone?.let { zone ->
                solatRepository.remoteFetchPrayerTimes(
                    zone = zone,
                    year = currentDate[0].toInt(),
                    month = currentDate[1].toInt()
                )
            }
        }
    }
}

sealed interface PrayerTimeUiState {
    data object Loading : PrayerTimeUiState

    data class Error(
        val message: String
    ) : PrayerTimeUiState

    data class Success(
        val prayerTime: List<PrayerDateTime> = emptyList()
    ) : PrayerTimeUiState
}

data class PrayerDateTime(
    val day: Int,
    val hijri: String,
    val prayer: String,
    val time: Long
)
