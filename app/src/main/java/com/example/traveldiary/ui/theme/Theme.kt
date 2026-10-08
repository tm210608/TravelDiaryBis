package com.example.traveldiary.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> DarkColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = DiaryTypography,
        shapes = DiaryShapes,
        content = content
    )
}
