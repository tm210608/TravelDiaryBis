package com.example.traveldiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DiaryPrimary,
    onPrimary = DiaryOnPrimary,
    primaryContainer = DiaryPrimaryContainer,
    onPrimaryContainer = DiaryOnPrimaryContainer,
    inversePrimary = DiaryInversePrimary,
    secondary = DiarySecondary,
    onSecondary = DiaryOnSecondary,
    secondaryContainer = DiarySecondaryContainer,
    onSecondaryContainer = DiaryOnSecondaryContainer,
    tertiary = DiaryTertiary,
    onTertiary = DiaryOnTertiary,
    tertiaryContainer = DiaryTertiaryContainer,
    onTertiaryContainer = DiaryOnTertiaryContainer,
    error = DiaryError,
    onError = DiaryOnError,
    errorContainer = DiaryErrorContainer,
    onErrorContainer = DiaryOnErrorContainer,
    background = DiarySurfaceDim,
    onBackground = DiaryOnSurface,
    surface = DiarySurfaceDim,
    onSurface = DiaryOnSurface,
    surfaceVariant = DiarySurfaceContainerHighest,
    onSurfaceVariant = DiaryOnSurfaceVariant,
    surfaceTint = DiarySurfaceTint,
    surfaceDim = DiarySurfaceDim,
    surfaceBright = DiarySurfaceBright,
    surfaceContainerLowest = DiarySurfaceContainerLowest,
    surfaceContainerLow = DiarySurfaceContainerLow,
    surfaceContainer = DiarySurfaceContainer,
    surfaceContainerHigh = DiarySurfaceContainerHigh,
    surfaceContainerHighest = DiarySurfaceContainerHighest,
    inverseSurface = DiaryInverseSurface,
    inverseOnSurface = DiaryInverseOnSurface,
    outline = DiaryOutline,
    outlineVariant = DiaryOutlineVariant,
    primaryFixed = DiaryPrimaryFixed,
    primaryFixedDim = DiaryPrimaryFixedDim,
    onPrimaryFixed = DiaryOnPrimaryFixed,
    onPrimaryFixedVariant = DiaryOnPrimaryFixedVariant,
    secondaryFixed = DiarySecondaryFixed,
    secondaryFixedDim = DiarySecondaryFixedDim,
    onSecondaryFixed = DiaryOnSecondaryFixed,
    onSecondaryFixedVariant = DiaryOnSecondaryFixedVariant,
    tertiaryFixed = DiaryTertiaryFixed,
    tertiaryFixedDim = DiaryTertiaryFixedDim,
    onTertiaryFixed = DiaryOnTertiaryFixed,
    onTertiaryFixedVariant = DiaryOnTertiaryFixedVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = FieldVermilion,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = FieldVermilionContainer,
    onPrimaryContainer = FieldOnVermilionContainer,
    inversePrimary = FieldInversePrimary,
    secondary = FieldAmber,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = FieldAmberContainer,
    onSecondaryContainer = FieldOnAmberContainer,
    tertiary = FieldSlate,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = FieldSlateContainer,
    onTertiaryContainer = FieldOnSlateContainer,
    error = FieldError,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = FieldErrorContainer,
    onErrorContainer = FieldOnErrorContainer,
    background = FieldPaper,
    onBackground = FieldInk,
    surface = FieldPaper,
    onSurface = FieldInk,
    surfaceVariant = FieldSurfaceContainerHighest,
    onSurfaceVariant = FieldInkVariant,
    surfaceTint = FieldVermilion,
    surfaceDim = FieldSurfaceDim,
    surfaceBright = FieldSurfaceBright,
    surfaceContainerLowest = FieldSurfaceContainerLowest,
    surfaceContainerLow = FieldSurfaceContainerLow,
    surfaceContainer = FieldSurfaceContainer,
    surfaceContainerHigh = FieldSurfaceContainerHigh,
    surfaceContainerHighest = FieldSurfaceContainerHighest,
    inverseSurface = FieldInk,
    inverseOnSurface = FieldPaper,
    outline = FieldRuleStrong,
    outlineVariant = FieldRule,
    primaryFixed = FieldPrimaryFixed,
    primaryFixedDim = FieldPrimaryFixedDim,
    onPrimaryFixed = FieldOnPrimaryFixed,
    onPrimaryFixedVariant = FieldOnPrimaryFixedVariant,
    secondaryFixed = FieldSecondaryFixed,
    secondaryFixedDim = FieldSecondaryFixedDim,
    onSecondaryFixed = FieldOnSecondaryFixed,
    onSecondaryFixedVariant = FieldOnSecondaryFixedVariant,
    tertiaryFixed = FieldTertiaryFixed,
    tertiaryFixedDim = FieldTertiaryFixedDim,
    onTertiaryFixed = FieldOnTertiaryFixed,
    onTertiaryFixedVariant = FieldOnTertiaryFixedVariant,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = DiaryTypography,
        shapes = DiaryShapes,
        content = content
    )
}
