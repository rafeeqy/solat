package my.naqibrafiqi.solat.ui.feature.settings.zone

import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.util.states.UiText

data class ZoneSelectionUiState(
    val zones: List<Zone> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null,
    val isInternalLoading: Boolean = false,
    val isInternalError: Boolean = false
)
