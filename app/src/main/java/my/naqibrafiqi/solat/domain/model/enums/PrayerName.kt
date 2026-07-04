package my.naqibrafiqi.solat.domain.model.enums

import my.naqibrafiqi.solat.R

enum class PrayerName(val resId: Int) {
    IMSAK(resId = R.string.common_prayer_name_imsak),
    SUBUH(resId = R.string.common_prayer_name_fajr),
    SYURUK(resId = R.string.common_prayer_name_syuruk),
    DHUHA(resId = R.string.common_prayer_name_dhuha),
    ZOHOR(resId = R.string.common_prayer_name_dhuhr),
    ASAR(resId = R.string.common_prayer_name_asr),
    MAGHRIB(resId = R.string.common_prayer_name_maghrib),
    ISYAK(resId = R.string.common_prayer_name_isha)
}