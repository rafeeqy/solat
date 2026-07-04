package my.naqibrafiqi.solat.domain.model.enums

import my.naqibrafiqi.solat.R

enum class AppLanguage(
    val label: String,
    val resId: Int
) {
    ENGLISH(
        label = "English",
        resId = R.string.common_locale_english
    ),
    MALAY(
        label = "Bahasa Melayu",
        resId = R.string.common_locale_bahasa_melayu
    ),
    JAWI(
        label = "Bahasa Melayu (Jawi)",
        resId = R.string.common_locale_jawi
    )
}
