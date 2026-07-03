package my.naqibrafiqi.solat.domain.model

import java.time.Instant

data class Zone(
    val id: Int,
    val code: String,
    val state: String,
    val district: String,
    val createdDate: Instant,
    val updatedDate: Instant? = null,
    val deletedDate: Instant? = null
)
