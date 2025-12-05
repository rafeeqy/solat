package com.example.solat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ZoneApiModel(
    val jakimCode: String,
    val negeri: String,
    val daerah: String
)
