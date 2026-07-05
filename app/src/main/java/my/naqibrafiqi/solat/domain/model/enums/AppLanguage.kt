package my.naqibrafiqi.solat.domain.model.enums

import my.naqibrafiqi.solat.R

enum class AppLanguage(
    val langCode: String,
    val resId: Int
) {
    ENGLISH(
        langCode = "en",
        resId = R.string.common_locale_english
    ),
    MALAY(
        langCode = "ms",
        resId = R.string.common_locale_bahasa_melayu
    ),
    JAWI(
        langCode = "ms-Arab",
        resId = R.string.common_locale_jawi
    )
}
