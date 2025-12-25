package com.example.solat.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.solat.ui.viewmodel.WaktuSolat
import javax.inject.Inject

class OfflineAlarmSchedulerRepository @Inject constructor(
    private val context: Context
) : AlarmSchedulerRepository {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(prayer: WaktuSolat) {
        val intent = Intent(context, AlarmReceiver::class.java)
            .apply {
                putExtra(
                    "PRAYER_NAME",
                    prayer.name
                )
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayer.name.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            prayer.time,
            pendingIntent
        )
    }

    override fun cancel(prayer: WaktuSolat) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayer.name.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}