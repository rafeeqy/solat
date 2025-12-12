package com.example.solat.ui.screen.onboarding

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.solat.ui.screen.permission.PermissionHandler
import com.example.solat.ui.theme.SolatTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(
    onNavigateToZoneLocation: () -> Unit,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                                text = "No zone selected",
                                color = MaterialTheme.colorScheme.secondary,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
                Button(
                    onClick = onNavigateToMain,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
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
