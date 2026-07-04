package my.naqibrafiqi.solat.ui.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.BuildConfig
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.domain.model.enums.AppLanguage
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.ui.component.SelectionDialog
import my.naqibrafiqi.solat.ui.component.SelectionDialogItem
import my.naqibrafiqi.solat.ui.theme.SolatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToZoneLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val zone = viewModel.zone.collectAsStateWithLifecycle().value

    val versionName = BuildConfig.VERSION_NAME
    val buildTime = BuildConfig.BUILD_TIME.lowercase()

    var showTheme by remember { mutableStateOf(false) }

    var showLocale by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Arrow back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Location
                item {
                    SettingsItem(
                        title = stringResource(id = R.string.settings_label_location),
                        subtitle = if (zone == null) {
                            stringResource(id = R.string.common_label_unknown_zone)
                        } else {
                            "${zone.state} (${zone.code}): ${zone.district}"
                        },
                        icon = Icons.Rounded.LocationOn,
                        onClick = onNavigateToZoneLocation,
                        navigationIndicator = true
                    )
                }

                // App theme
                item {
                    SettingsItem(
                        title = stringResource(id = R.string.settings_label_theme),
                        subtitle = stringResource(id = ThemeSettings.SYSTEM.resId),
                        icon = Icons.Rounded.Palette,
                        onClick = { showTheme = true }
                    )
                }

                // App locale
                item {
                    SettingsItem(
                        title = stringResource(id = R.string.settings_label_locale),
                        subtitle = stringResource(id = AppLanguage.ENGLISH.resId),
                        icon = Icons.Rounded.Language,
                        onClick = { showLocale = true }
                    )
                }

                // Notification
                item {
                    SettingsItem(
                        title = stringResource(id = R.string.settings_label_notification),
                        subtitle = stringResource(id = R.string.settings_label_notification_description),
                        icon = Icons.Rounded.Notifications,
                        onClick = { /* TODO: Navigate to system notification settings screen */ },
                        navigationIndicator = true
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "v$versionName ($buildTime)",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row {
                    IconButton(onClick = { /* TODO: Open github repo */ }) {
                        Image(
                            painter = painterResource(if (isSystemInDarkTheme()) R.drawable.github_white else R.drawable.github),
                            contentDescription = "Github",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    // Theme selection dialog
    if (showTheme) {
        SelectionDialog(
            title = stringResource(id = R.string.settings_dialog_title_theme),
            selectedItem = ThemeSettings.SYSTEM, // TODO: Current theme
            selectionItems = ThemeSettings.entries.map { theme ->
                SelectionDialogItem(
                    id = 0,
                    value = theme,
                    label = stringResource(id = theme.resId)
                )
            },
            onDismiss = { showTheme = false },
            onSelect = { theme ->
                showTheme = false
                /* TODO: Update theme */
            }
        )
    }

    // Language selection dialog
    if (showLocale) {
        SelectionDialog(
            title = stringResource(id = R.string.settings_dialog_title_language),
            selectedItem = AppLanguage.ENGLISH, // TODO: Current language
            selectionItems = AppLanguage.entries.map { lang ->
                SelectionDialogItem(
                    id = 0,
                    value = lang,
                    label = stringResource(id = lang.resId)
                )
            },
            onDismiss = { showLocale = false },
            onSelect = { lang ->
                showLocale = false
                /* TODO: Update language */
            }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIndicator: Boolean = false
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        modifier = modifier.clickable(
            enabled = true,
            role = Role.DropdownList,
            onClick = onClick
        ),
        supportingContent = {
            Text(
                text = subtitle,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = if (navigationIndicator) {
            {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Chevron right"
                )
            }
        } else {
            null
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SolatTheme {
        SettingsScreen(
            onNavigateBack = {},
            onNavigateToZoneLocation = {}
        )
    }
}
