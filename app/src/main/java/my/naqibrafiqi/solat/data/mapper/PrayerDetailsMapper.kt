package my.naqibrafiqi.solat.data.mapper

import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.remote.model.PrayerDetailsDto
import java.time.Instant

fun PrayerDetailsDto.toEntity(zoneId: Int): PrayerDetailsEntity =
    PrayerDetailsEntity(
        zoneId = zoneId,
        year = year,
        month = month,
        monthNumber = monthNumber,
        apiLastUpdated = lastUpdated,
        createdDate = Instant.now().toEpochMilli(),
        updatedDate = null,
        deletedDate = null
    )
