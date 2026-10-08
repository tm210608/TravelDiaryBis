package com.example.traveldiary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.traveldiary.R

// Fuentes variables OFL en res/font; el peso se fija por eje 'wght' (API 26+).
@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: Int, fontWeight: FontWeight) = Font(
    resId = resId,
    weight = fontWeight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val EpilogueFamily = FontFamily(
    variableFont(R.font.epilogue, 600, FontWeight.SemiBold),
    variableFont(R.font.epilogue, 700, FontWeight.Bold),
    variableFont(R.font.epilogue, 800, FontWeight.ExtraBold),
)

val ManropeFamily = FontFamily(
    variableFont(R.font.manrope, 400, FontWeight.Normal),
    variableFont(R.font.manrope, 500, FontWeight.Medium),
    variableFont(R.font.manrope, 600, FontWeight.SemiBold),
    variableFont(R.font.manrope, 700, FontWeight.Bold),
)

private fun epilogue(size: Int, line: Int, weight: FontWeight, spacingEm: Float = 0f) =
    TextStyle(
        fontFamily = EpilogueFamily,
        fontSize = size.sp,
        lineHeight = line.sp,
        fontWeight = weight,
        letterSpacing = spacingEm.em,
    )

private fun manrope(size: Int, line: Int, weight: FontWeight, spacingEm: Float = 0f) =
    TextStyle(
        fontFamily = ManropeFamily,
        fontSize = size.sp,
        lineHeight = line.sp,
        fontWeight = weight,
        letterSpacing = spacingEm.em,
    )

// Escala de docs/specs/m3-expressive-redesign/stitch/DESIGN.md
val DiaryTypography = Typography(
    displayLarge = epilogue(48, 56, FontWeight.ExtraBold, -0.02f),
    displayMedium = epilogue(36, 44, FontWeight.Bold, -0.015f),
    headlineLarge = epilogue(32, 40, FontWeight.Bold, -0.01f),
    headlineMedium = epilogue(24, 32, FontWeight.SemiBold),
    headlineSmall = epilogue(20, 28, FontWeight.SemiBold),
    titleLarge = epilogue(18, 26, FontWeight.SemiBold),
    titleMedium = manrope(16, 24, FontWeight.SemiBold),
    titleSmall = manrope(14, 20, FontWeight.SemiBold),
    bodyLarge = manrope(16, 26, FontWeight.Normal),
    bodyMedium = manrope(14, 22, FontWeight.Normal),
    bodySmall = manrope(12, 18, FontWeight.Normal),
    labelLarge = manrope(14, 20, FontWeight.SemiBold, 0.01f),
    labelMedium = manrope(12, 16, FontWeight.SemiBold, 0.02f),
    labelSmall = manrope(10, 14, FontWeight.Bold, 0.05f),
)
