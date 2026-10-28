package com.example.traveldiary.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.traveldiary.domain.model.RouteStamp
import com.example.traveldiary.domain.model.formatToTicket
import com.example.traveldiary.ui.theme.MonoEyebrow
import com.example.traveldiary.ui.theme.MonoStubMeta

@Composable
fun PassportStamp(
    stamp: RouteStamp,
    ink: Color,
    modifier: Modifier = Modifier,
) {
    val date = stamp.firstVisit.formatToTicket()
    Box(
        modifier = modifier
            .rotate(stamp.rotation)
            .alpha(0.75f)
            .border(2.dp, ink, RoundedCornerShape(6.dp))
            .padding(3.dp)
            .border(1.dp, ink, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stamp.code,
                style = MonoEyebrow.copy(fontSize = MonoEyebrow.fontSize * 1.6f),
                color = ink,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stamp.label,
                style = MonoStubMeta,
                color = ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${date.day} ${date.month} ${date.yearShort}",
                style = MonoStubMeta,
                color = ink,
            )
        }
    }
}
