package com.example.solat.jobs.alarm

import com.example.solat.data.repository.Prayer
import com.example.solat.ui.viewmodel.WaktuSolat

interface AlarmSchedulerRepository {
    fun schedule(
        zoneId: Int,
        zoneCode: String,
        nextPrayer: Long
    )

    fun cancel(prayer: WaktuSolat)
}