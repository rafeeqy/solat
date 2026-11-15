package my.naqibrafiqi.solat.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import my.naqibrafiqi.solat.MainActivity
import my.naqibrafiqi.solat.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolatNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {

    companion object {
        private const val CHANNEL_ID = "SOLAT_NOTIFICATIONS"
    }

    fun showNotification(
        notificationId: Int,
        title: String,
        message: String,
        deepLinkUri: String? = null
    ) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val pendingIntent = createPendingIntent(deepLinkUri)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        // Created on every notification, which also updates the name and description shown in
        // the system's notification settings to the current language.
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply { description = context.getString(R.string.notification_channel_description) }

        notificationManager.createNotificationChannel(channel)
    }

    private fun createPendingIntent(deepLinkUri: String?): PendingIntent {
        val intent = if (deepLinkUri != null) {
            Intent(Intent.ACTION_VIEW, deepLinkUri.toUri()).apply {
                setPackage(context.packageName)
            }
        } else {
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val requestCode = deepLinkUri?.hashCode() ?: 0

        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
}
