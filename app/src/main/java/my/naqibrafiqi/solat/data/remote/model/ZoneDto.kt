package my.naqibrafiqi.solat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ZoneDto(
    val jakimCode: String,
    val negeri: String,
    val daerah: String
)
