package my.naqibrafiqi.solat.domain.model.enums

import my.naqibrafiqi.solat.R

enum class PrayerName(
    val resId: Int,
    val prayerName: String
) {
    IMSAK(
        resId = R.string.common_prayer_name_imsak,
        prayerName = "Imsak"
    ),
    SUBUH(
        resId = R.string.common_prayer_name_fajr,
        prayerName = "Subuh"
    ),
    SYURUK(
        resId = R.string.common_prayer_name_syuruk,
        prayerName = "Syuruk"
    ),
    DHUHA(
        resId = R.string.common_prayer_name_dhuha,
        prayerName = "Dhuha"
    ),
    ZOHOR(
        resId = R.string.common_prayer_name_dhuhr,
        prayerName = "Zohor"
    ),
    ASAR(
        resId = R.string.common_prayer_name_asr,
        prayerName = "Asar"
    ),
    MAGHRIB(
        resId = R.string.common_prayer_name_maghrib,
        prayerName = "Maghrib"
    ),
    ISYAK(
        resId = R.string.common_prayer_name_isha,
        prayerName = "Isyak"
    )
}