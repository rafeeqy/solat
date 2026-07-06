package my.naqibrafiqi.solat.ui.feature.settings.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.util.permission.hasExactAlarmPermission
import my.naqibrafiqi.solat.util.permission.hasNotificationPermission
import my.naqibrafiqi.solat.util.permission.requestExactAlarmPermission
import my.naqibrafiqi.solat.util.permission.requestNotificationPermission
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    snackbarManager: SnackbarManager,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val enabledPrayers = viewModel.enabledPrayers.collectAsStateWithLifecycle().value

    val context = LocalContext.current

    var isNotificationGranted by remember { mutableStateOf(hasNotificationPermission(context)) }
    var isAlarmGranted by remember { mutableStateOf(hasExactAlarmPermission(context)) }

    // Re-evaluate permissions every time the screen become active
    LifecycleResumeEffect(Unit) {
        isNotificationGranted = hasNotificationPermission(context)
        isAlarmGranted = hasExactAlarmPermission(context)

        onPauseOrDispose { /* NOTE: Do nothing... */ }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notification_title_notification),
                        style = MaterialTheme.typography.titleLarge
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.notification_label_permissions),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Column {
                        ListItem(
                            headlineContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.permission_label_enable_notifications),
                                        color = if (isNotificationGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )

                                    Icon(
                                        imageVector = if (isNotificationGranted) Icons.Rounded.Check else Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = if (isNotificationGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.clickable(
                                enabled = true,
                                role = Role.DropdownList,
                                onClick = { requestNotificationPermission(context) }
                            ),
                            supportingContent = {
                                Text(text = stringResource(id = R.string.permission_label_enable_notifications_description))
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                        )

                        ListItem(
                            headlineContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.permission_label_enable_alarm),
                                        color = if (isAlarmGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )

                                    Icon(
                                        imageVector = if (isAlarmGranted) Icons.Rounded.Check else Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = if (isAlarmGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.clickable(
                                enabled = true,
                                role = Role.DropdownList,
                                onClick = { requestExactAlarmPermission(context) }
                            ),
                            supportingContent = {
                                Text(text = stringResource(id = R.string.permission_label_enable_alarm_description))
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Alarm,
                                    contentDescription = "Alarm"
                                )
                            }
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.notification_label_prayers_notification),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Column {
                        PrayerName.entries.forEach { prayerName ->
                            val isEnabled = enabledPrayers.contains(prayerName.name)

                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = stringResource(id = prayerName.resId),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.clickable(
                                    enabled = true,
                                    role = Role.DropdownList,
                                    onClick = { viewModel.onPrayerToggle(prayerName, !isEnabled) }
                                ),
                                trailingContent = {
                                    Switch(
                                        checked = isEnabled,
                                        onCheckedChange = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
