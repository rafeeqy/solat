package my.naqibrafiqi.solat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import my.naqibrafiqi.solat.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val MontserratFont = GoogleFont("Montserrat")

val AmiriFont = GoogleFont("Amiri")

val MontserratWithAmiriFontFamily = FontFamily(
    // Normal
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Thin
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.ExtraLight
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Light
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = AmiriFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Medium
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    ),
    Font(
        googleFont = AmiriFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.ExtraBold
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Black
    ),
    // Italic
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Thin,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Light,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = AmiriFont,
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Medium,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.SemiBold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = AmiriFont,
        fontProvider = provider,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = MontserratFont,
        fontProvider = provider,
        weight = FontWeight.Black,
        style = FontStyle.Italic
    )
)

val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = MontserratWithAmiriFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = MontserratWithAmiriFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = MontserratWithAmiriFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = MontserratWithAmiriFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = MontserratWithAmiriFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = MontserratWithAmiriFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = MontserratWithAmiriFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = MontserratWithAmiriFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = MontserratWithAmiriFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = MontserratWithAmiriFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = MontserratWithAmiriFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = MontserratWithAmiriFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = MontserratWithAmiriFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = MontserratWithAmiriFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = MontserratWithAmiriFontFamily)
)
