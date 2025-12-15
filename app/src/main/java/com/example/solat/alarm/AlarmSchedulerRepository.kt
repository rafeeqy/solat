package com.example.solat.alarm

import com.example.solat.ui.viewmodel.Prayer

interface AlarmSchedulerRepository {
    fun schedule(prayer: Prayer)

    fun cancel(prayer: Prayer)
}