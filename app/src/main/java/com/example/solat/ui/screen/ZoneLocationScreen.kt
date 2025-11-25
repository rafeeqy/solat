package com.example.solat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.solat.ui.component.EmptyContent
import com.example.solat.ui.component.ErrorContent
import com.example.solat.ui.component.LoadingContent
import com.example.solat.ui.theme.SolatTheme
import com.example.solat.ui.viewmodel.ZoneLocationUiState
import com.example.solat.ui.viewmodel.ZoneLocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneLocationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ZoneLocationViewModel = hiltViewModel()
) {
    val currZoneId = viewModel.currentZoneId
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val snackbarState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Select prayer zone") },
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
        snackbarHost = { SnackbarHost(hostState = snackbarState) }
    ) { innerPadding ->
        when (uiState) {
            is ZoneLocationUiState.Loading -> {
                LoadingContent()
            }

            is ZoneLocationUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }

            is ZoneLocationUiState.Success -> {
                val zones = uiState.zones

                if (zones.isEmpty()) {
                    EmptyContent(
                        message = "No zone location to display.",
                        onRefresh = { viewModel.refresh() }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    ) {
                        zones.groupBy { it.state }.forEach { (state, zones) ->
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
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
                                key = { it.zoneId }
                            ) { zone ->
                                ListItem(
                                    headlineContent = { Text(text = "Zone: ${zone.code}") },
                                    modifier = Modifier.clickable(
                                        enabled = true,
                                        role = Role.DropdownList,
                                        onClick = {
                                            coroutineScope.launch(context = Dispatchers.IO) {
                                                val result = viewModel.selectZone(zone)

                                                snackbarState.showSnackbar(
                                                    message = result.message,
                                                    withDismissAction = true,
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    ),
                                    supportingContent = { Text(text = zone.district) },
                                    trailingContent = {
                                        RadioButton(
                                            selected = zone.zoneId == currZoneId,
                                            onClick = null
                                        )
                                    },
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                    }
                }
            }
        }
    }
}
