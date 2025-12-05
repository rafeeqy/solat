package com.example.solat.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.solat.MainActivity
import com.example.solat.R

private const val CHANNEL_ID = "PRAYER_NOTIFICATION"
private const val NOTIFICATION_ID = 1
private const val NOTIFICATION_NAME = "Prayer Time Notifications"
private const val NOTIFICATION_DESCRIPTION = "Shows notifications when it is prayer time"

fun makePrayerNotification(
    title: String,
    message: String,
    context: Context
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // NOTE: Create the `NotificationChannel` only on API 26+
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(
            CHANNEL_ID,
            NOTIFICATION_NAME,
            importance
        )
        channel.description = NOTIFICATION_DESCRIPTION

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = createPendingIntent(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context)
        .notify(
            NOTIFICATION_ID,
            builder.build()
        )
}

private fun createPendingIntent(context: Context): PendingIntent {
    val intent = Intent(
        context,
        MainActivity::class.java
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    var flags = PendingIntent.FLAG_UPDATE_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }

    return PendingIntent.getActivity(
        context,
        0,
        intent,
        flags
    )
}
