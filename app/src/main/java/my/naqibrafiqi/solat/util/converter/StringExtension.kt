package my.naqibrafiqi.solat.util.converter

fun String.toJawiNumerals(): String {
    val easternArabic = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    return this.map { char ->
        if (char in '0'..'9') {
            easternArabic[char - '0']
        } else {
            char
        }
    }.joinToString("")
}
