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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.ui.component.SelectionDialog
import my.naqibrafiqi.solat.ui.component.SelectionDialogItem
import my.naqibrafiqi.solat.ui.theme.SolatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToZoneLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var openThemeSelection by remember { mutableStateOf(false) }

    var openLanguageSelection by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
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
                    ListItem(
                        headlineContent = { Text(text = "Location") },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = onNavigateToZoneLocation
                        ),
                        supportingContent = { Text(text = "Set location based on JAKIM zones") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Location on"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "Chevron right"
                            )
                        }
                    )
                }
                // App theme
                item {
                    ListItem(
                        headlineContent = { Text(text = "Theme") },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = { openThemeSelection = true }
                        ),
                        supportingContent = {
                            Text(
                                text = buildAnnotatedString {
                                    append("Current theme: ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("System")
                                    }
                                }
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = "Palette"
                            )
                        }
                    )
                }
                // App locale
                item {
                    ListItem(
                        headlineContent = { Text(text = "Language") },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = { openLanguageSelection = true }
                        ),
                        supportingContent = {
                            Text(
                                text = buildAnnotatedString {
                                    append("Current language: ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("English")
                                    }
                                }
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = "Language"
                            )
                        }
                    )
                }
                // Notification
                item {
                    ListItem(
                        headlineContent = { Text(text = "Notification") },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = { /* TODO: Navigate to system notification setting screen */ }
                        ),
                        supportingContent = { Text(text = "Manage notification preferences") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Notifications"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "Chevron right"
                            )
                        }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Solat",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "v0.0.6 (2025-12-25 14:21:00)",
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

    if (openThemeSelection) {
        SelectionDialog(
            title = "Theme",
            selectedItem = SelectionDialogItem(
                id = 1,
                value = "system",
                label = "System"
            ), // TODO: Current theme
            selectionItems = listOf(
                SelectionDialogItem(
                    id = 1,
                    value = "system",
                    label = "System"
                ),
                SelectionDialogItem(
                    id = 2,
                    value = "light",
                    label = "Light"
                ),
                SelectionDialogItem(
                    id = 3,
                    value = "dark",
                    label = "Dark"
                )
            ), // TODO: Theme selection list
            onDismiss = { openThemeSelection = false },
            onSelect = { /* TODO: Update theme */ }
        )
    }

    if (openLanguageSelection) {
        SelectionDialog(
            title = "Language",
            selectedItem = SelectionDialogItem(
                id = 1,
                value = "eng",
                label = "English"
            ), // TODO: Current language
            selectionItems = listOf(
                SelectionDialogItem(
                    id = 1,
                    value = "eng",
                    label = "English"
                ),
                SelectionDialogItem(
                    id = 2,
                    value = "my",
                    label = "Malay"
                ),
                SelectionDialogItem(
                    id = 3,
                    value = "my-jw",
                    label = "Malay (Jawi)"
                )
            ), // TODO: Language selection list
            onDismiss = { openLanguageSelection = false },
            onSelect = { /* TODO: Update language */ }
        )
    }
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
