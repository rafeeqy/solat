package my.naqibrafiqi.solat.data.mapper

import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.remote.model.ZoneDto
import my.naqibrafiqi.solat.domain.model.Zone
import java.time.Instant

fun ZoneEntity.toDomain(): Zone =
    Zone(
        id = zoneId,
        code = code,
        state = state,
        district = district,
        createdDate = Instant.ofEpochMilli(createdDate),
        updatedDate = updatedDate?.let { Instant.ofEpochMilli(it) },
        deletedDate = deletedDate?.let { Instant.ofEpochMilli(it) }
    )

fun ZoneDto.toEntity(): ZoneEntity =
    ZoneEntity(
        code = jakimCode,
        state = negeri,
        district = daerah,
        createdDate = Instant.now().toEpochMilli()
    )
