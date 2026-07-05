package my.naqibrafiqi.solat.ui.feature.settings.zone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.ui.component.EmptyContent
import my.naqibrafiqi.solat.ui.component.LinearLoadingIndicator
import my.naqibrafiqi.solat.ui.component.LoadingMessage
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager
import my.naqibrafiqi.solat.util.states.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectionScreen(
    snackbarManager: SnackbarManager,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToDailyPrayer: (() -> Unit)? = null,
    viewModel: ZoneSelectionViewModel = hiltViewModel()
) {
    val currentZoneCode = viewModel.currentZoneCode.collectAsStateWithLifecycle().value
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val zones = uiState.zones
    val loading = uiState.isLoading

    val listState = rememberLazyListState()

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    LaunchedEffect(Unit) {
        launch {
            snapshotFlow { uiState.errorMessage }.collect { message ->
                message?.let { snackbarManager.showSnackbar(message = it) }
            }
        }

        launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.Error -> {
                        snackbarManager.showSnackbar(message = event.errorMessage)
                    }

                    is UiEvent.Loading -> {
                        // NOTE: Do nothing...
                    }

                    is UiEvent.Success -> {
                        snackbarManager.showSnackbar(message = event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.zone_selection_title),
                        style = if (isRtl) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Arrow back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarManager.snackbarState)
        },
        bottomBar = onNavigateToDailyPrayer?.let { toDailyPrayer ->
            {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 32.dp
                        )
                ) {
                    Button(
                        onClick = toDailyPrayer,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentZoneCode != null
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.onboarding_button_finished_set_up),
                                fontWeight = FontWeight.Medium,
                                style = if (isRtl) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
                            )

                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Check"
                            )
                        }
                    }
                }
            }
        } ?: {}
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            state = listState,
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
        ) {
            stickyHeader {
                LinearLoadingIndicator(isLoading = loading)
            }

            if (loading) {
                item {
                    LoadingMessage(modifier = Modifier.padding(top = 32.dp))
                }
            }

            if (zones.isEmpty() && !loading) {
                item {
                    EmptyContent(
                        message = stringResource(id = R.string.common_results_no_zones),
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            } else {
                zones.groupBy { it.state }.forEach { (state, zones) ->
                    stickyHeader {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                )
                        ) {
                            Text(
                                text = state,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    items(
                        items = zones,
                        key = { it.id }
                    ) { zone ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        id = R.string.zone_selection_label_zone_code,
                                        zone.code
                                    ),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier.clickable(
                                enabled = true,
                                role = Role.DropdownList,
                                onClick = {
                                    if (zone.code != currentZoneCode) {
                                        viewModel.selectZone(zone = zone)
                                    }
                                }
                            ),
                            supportingContent = { Text(text = zone.district) },
                            trailingContent = {
                                RadioButton(
                                    selected = zone.code == currentZoneCode,
                                    onClick = null
                                )
                            },
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
