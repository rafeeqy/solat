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
import androidx.compose.material.icons.rounded.ChevronLeft
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.BuildConfig
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.domain.model.enums.AppLanguage
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.ui.component.SelectionDialog
import my.naqibrafiqi.solat.ui.component.SelectionDialogItem
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager
import my.naqibrafiqi.solat.util.states.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    snackbarManager: SnackbarManager,
    onNavigateBack: () -> Unit,
    onNavigateToZoneLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val zone = viewModel.zone.collectAsStateWithLifecycle().value
    val theme = viewModel.theme.collectAsStateWithLifecycle().value
    val locale = viewModel.getLocale()

    val versionName = BuildConfig.VERSION_NAME
    val buildTime = BuildConfig.BUILD_TIME.lowercase()

    var showTheme by remember { mutableStateOf(false) }

    var showLocale by remember { mutableStateOf(false) }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    snackbarManager.showSnackbar(message = event.errorMessage)
                }

                is UiEvent.Loading -> {
                    // NOTE: Do nothing...
                }

                is UiEvent.Success<*> -> {
                    snackbarManager.showSnackbar(message = event.message)
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
                        text = stringResource(id = R.string.settings_title_settings),
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
                        subtitle = stringResource(id = theme.resId),
                        icon = Icons.Rounded.Palette,
                        onClick = { showTheme = true }
                    )
                }

                // App locale
                item {
                    SettingsItem(
                        title = stringResource(id = R.string.settings_label_locale),
                        subtitle = stringResource(id = locale.resId),
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
                        style = if (isRtl) MaterialTheme.typography.titleLarge else MaterialTheme.typography.labelLarge
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
            selectedItem = theme,
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
                viewModel.updateTheme(theme.value)
            }
        )
    }

    // Language selection dialog
    if (showLocale) {
        SelectionDialog(
            title = stringResource(id = R.string.settings_dialog_title_language),
            selectedItem = locale,
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
                viewModel.updateLocale(lang.value)
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
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = if (isRtl) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
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
                style = if (isRtl) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.labelLarge
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
                    imageVector = if (isRtl) Icons.Rounded.ChevronLeft else Icons.Rounded.ChevronRight,
                    contentDescription = if (isRtl) "Chevron left" else "Chevron right"
                )
            }
        } else {
            null
        }
    )
}
