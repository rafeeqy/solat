package com.example.solat.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneApiModel(
    @SerialName(value = "jakim_code")
    val jakimCode: String,
    val negeri: String,
    val daerah: String
)
