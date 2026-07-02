package my.naqibrafiqi.solat.ui.feature.onboarding

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler() {
    val context = LocalContext.current

    // NOTE: Alarms and reminders permission
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    var alarmPermissionEnabled by rememberSaveable { mutableStateOf(alarmManager?.canScheduleExactAlarms() == true) }

    // NOTE: Post Notifications permission
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    var notificationPermissionEnabled by rememberSaveable { mutableStateOf(notificationPermission.status.isGranted) }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            alarmPermissionEnabled = alarmManager?.canScheduleExactAlarms() == true
            notificationPermissionEnabled = notificationPermission.status.isGranted == true
        }
    )

    if (!alarmPermissionEnabled) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.fromParts("package", context.packageName, null)
                        )

                        settingsLauncher.launch(intent)
                    }
                ) {
                    Text(text = "Grant permission")
                }
            },
            text = {
                Text(
                    text = "Alarms & reminders permission are required for this app to function properly.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }

    if (!notificationPermissionEnabled) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = {
                        notificationPermission.launchPermissionRequest()
                        notificationPermissionEnabled = true
                    }
                ) {
                    Text(text = "Grant permission")
                }
            },
            text = {
                Text(
                    text = "Notifications permission are required for this app to function properly.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}
