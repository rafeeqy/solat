package com.example.solat.jobs.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.solat.data.repository.PrayerNames
import com.example.solat.data.repository.SolatRepository
import com.example.solat.ui.viewmodel.WaktuSolat
import com.example.solat.util.SolatDateTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OfflineAlarmSchedulerRepository @Inject constructor(
    private val context: Context,
    private val solatRepository: SolatRepository
) : AlarmSchedulerRepository {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(
        zoneId: Int,
        zoneCode: String,
        nextPrayer: Long
    ) {
        var todayPrayers =
            solatRepository.fetchCurrentPrayer(
                zoneId,
                zoneCode,
                SolatDateTime.nowEpochMilli()
            )

        todayPrayers?.prayers?.get(PrayerNames.ISHA)?.let {
            if (System.currentTimeMillis() > it) {
                todayPrayers = solatRepository.fetchCurrentPrayer(
                    zoneId,
                    zoneCode,
                    SolatDateTime.tomorrowEpochMilli()
                )
            }
        }

        val nextPrayer =
            todayPrayers?.prayers?.entries?.find { it.value > System.currentTimeMillis() }

        if (nextPrayer != null) {
            val intent = Intent(context, AlarmReceiver::class.java)
                .apply {
                    putExtra(
                        PrayerIntentExtraKeys.PRAYER_NAME.name,
                        nextPrayer.key.label
                    )
                    putExtra(PrayerIntentExtraKeys.PRAYER_TIME.name, nextPrayer.value)
                    putExtra(PrayerIntentExtraKeys.ZONE_LOCATION.name, "TODO_ADD_CURR_LOCATION")
                    putExtra(PrayerIntentExtraKeys.ZONE_ID.name, zoneId)
                    putExtra(PrayerIntentExtraKeys.ZONE_CODE.name, zoneCode)
                }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                nextPrayer.key.name.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextPrayer.value,
                pendingIntent
            )
        }
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

enum class PrayerIntentExtraKeys {
    PRAYER_NAME, PRAYER_TIME, ZONE_LOCATION, ZONE_ID, ZONE_CODE
}
