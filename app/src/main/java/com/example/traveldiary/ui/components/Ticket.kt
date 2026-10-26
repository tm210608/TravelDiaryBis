package com.example.traveldiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun ticketShape(cornerRadius: Dp = 14.dp, notchRadius: Dp = 8.dp): Shape =
    object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline = with(density) {
            val cr = cornerRadius.toPx()
            val nr = notchRadius.toPx()
            val cy = size.height / 2f

            val punched = Path().apply {
                addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(cr)))
            }
            listOf(0f, size.width).forEach { cx ->
                val bite = Path().apply {
                    addOval(Rect(Offset(cx - nr, cy - nr), Size(nr * 2f, nr * 2f)))
                }
                punched.op(punched, bite, PathOperation.Difference)
            }
            Outline.Generic(punched)
        }
    }

@Composable
fun TicketDivider(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.width(1.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, size.height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 7f)),
        )
    }
}
