package my.naqibrafiqi.solat.domain.model.enums

import my.naqibrafiqi.solat.R

enum class ThemeSettings(val resId: Int) {
    SYSTEM(resId = R.string.common_theme_system),
    LIGHT(resId = R.string.common_theme_light),
    DARK(resId = R.string.common_theme_dark)
}
