package my.naqibrafiqi.solat.data.mapper

import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity
import my.naqibrafiqi.solat.data.remote.model.PrayerTimeDto
import java.time.Instant

fun PrayerTimeDto.toEntity(prayerDetailsId: Int): PrayerTimeEntity =
    PrayerTimeEntity(
        prayerDetailsId = prayerDetailsId,
        day = day,
        hijri = hijri,
        imsak = imsak,
        fajr = fajr,
        syuruk = syuruk,
        dhuha = dhuha,
        dhuhr = dhuhr,
        asr = asr,
        maghrib = maghrib,
        isha = isha,
        createdDate = Instant.now().toEpochMilli(),
        updatedDate = null,
        deletedDate = null
    )
