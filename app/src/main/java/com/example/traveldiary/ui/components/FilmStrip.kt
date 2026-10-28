package com.example.traveldiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val FilmBlack = Color(0xFF0E0D12)
val FilmHole = Color(0xFF26252F)
val FilmAmber = Color(0xFFFFBA20)

fun Modifier.filmStrip(
    backgroundColor: Color = FilmBlack,
    holeColor: Color = FilmHole,
): Modifier = this
    .background(backgroundColor)
    .drawBehind {
        val holeWidth = 7.dp.toPx()
        val holeHeight = 11.dp.toPx()
        val vGap = 9.dp.toPx()
        val marginX = 5.dp.toPx()
        val radius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        var y = 12.dp.toPx()
        while (y < size.height - holeHeight) {
            drawRoundRect(
                color = holeColor,
                topLeft = Offset(marginX, y),
                size = Size(holeWidth, holeHeight),
                cornerRadius = radius,
            )
            drawRoundRect(
                color = holeColor,
                topLeft = Offset(size.width - marginX - holeWidth, y),
                size = Size(holeWidth, holeHeight),
                cornerRadius = radius,
            )
            y += holeHeight + vGap
        }
    }
