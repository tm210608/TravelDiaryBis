package com.example.traveldiary.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.cartographyGrid(
    color: Color,
    cellSize: Dp = 48.dp,
    crossEvery: Int = 4,
): Modifier = this.drawBehind {
    val step = cellSize.toPx()
    if (step <= 0f) return@drawBehind
    val stroke = 0.8f

    var x = 0f
    while (x <= size.width) {
        drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = stroke)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
        y += step
    }

    val cross = 5.dp.toPx()
    val crossColor = color.copy(alpha = min(1f, color.alpha * 2.4f))
    var i = 0
    var cx = 0f
    while (cx <= size.width) {
        var j = 0
        var cy = 0f
        while (cy <= size.height) {
            if (i % crossEvery == 0 && j % crossEvery == 0) {
                drawLine(crossColor, Offset(cx - cross, cy), Offset(cx + cross, cy), stroke)
                drawLine(crossColor, Offset(cx, cy - cross), Offset(cx, cy + cross), stroke)
            }
            cy += step
            j++
        }
        cx += step
        i++
    }
}
