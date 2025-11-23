package com.example.solat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.solat.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val MontserratFont = GoogleFont("Montserrat")

val MontserratFontFamily = FontFamily(
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
        fontProvider = provider
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
    ),
)

val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = MontserratFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = MontserratFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = MontserratFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = MontserratFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = MontserratFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = MontserratFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = MontserratFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = MontserratFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = MontserratFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = MontserratFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = MontserratFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = MontserratFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = MontserratFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = MontserratFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = MontserratFontFamily)
)