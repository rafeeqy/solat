package com.example.solat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimeApiModel(
    val day: Int,
    val hijri: String,
    val fajr: Long,
    val syuruk: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long
)
