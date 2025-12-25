package com.example.solat.alarm

import com.example.solat.ui.viewmodel.WaktuSolat

interface AlarmSchedulerRepository {
    fun schedule(prayer: WaktuSolat)

    fun cancel(prayer: WaktuSolat)
}