package my.naqibrafiqi.solat.ui.screen.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.ui.screen.permission.PermissionHandler
import my.naqibrafiqi.solat.ui.theme.SolatTheme
import my.naqibrafiqi.solat.ui.viewmodel.OnboardingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(
    onNavigateToZoneLocation: () -> Unit,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val zoneId = viewModel.currentZoneId.collectAsStateWithLifecycle().value
    val selectedZone = viewModel.selectedZone.collectAsStateWithLifecycle().value

    Scaffold(modifier = modifier) { innerPadding ->

        PermissionHandler()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Solat",
                        modifier = Modifier.padding(bottom = 32.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = "Solat is an app to display and remind you of prayer times.",
                        modifier = Modifier.padding(horizontal = 56.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ListItem(
                        headlineContent = { Text(text = "Please select zone location") },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = onNavigateToZoneLocation
                        ),
                        supportingContent = {
                            Text(
                                text = if (selectedZone != null) "Zone ${selectedZone.code}: ${selectedZone.state}" else "No zone selected",
                                color = MaterialTheme.colorScheme.secondary,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        trailingContent = {
                            if (zoneId > 0) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Check circle",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Cancel,
                                    contentDescription = "Cancel",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }
                Button(
                    onClick = onNavigateToMain,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = zoneId > 0
                ) {
                    Text(
                        text = "Complete",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    SolatTheme {
        WelcomeScreen(
            onNavigateToZoneLocation = {},
            onNavigateToMain = {}
        )
    }
}
