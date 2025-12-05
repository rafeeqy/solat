package com.example.solat.alarm

import com.example.solat.ui.viewmodel.PrayerDateTime

interface AlarmSchedulerRepository {
    fun schedule(prayer: PrayerDateTime)

    fun cancel(prayer: PrayerDateTime)
}