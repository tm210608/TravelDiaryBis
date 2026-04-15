package com.example.traveldiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    secondary = SecondaryBlue,
    tertiary = CoolGray,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2D5AF0), // Azul vibrante para el día
    secondary = Color(0xFF00BFA5),
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSurface = Color.Black
)

@Composable
fun TravelDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
