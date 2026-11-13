package com.example.traveldiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.traveldiary.ui.theme.MonoEyebrow
import com.example.traveldiary.ui.theme.SerifAppTitle

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            if (eyebrow != null) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MonoEyebrow,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = "[$actionLabel]",
            style = MonoEyebrow,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun PostalHeader(
    title: String,
    photoUri: String?,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = SerifAppTitle,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bannerSurface =
        if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerLow
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        }
    val primaryColor = MaterialTheme.colorScheme.primary
    val stripeAccentColor =
        if (isDark) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.tertiary
        }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bannerSurface,
        shadowElevation = 2.dp,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 6.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                    RoundedCornerShape(20.dp),
                ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = titleStyle,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(Modifier.width(10.dp))
                        PostalStampMark(primaryColor = primaryColor)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .width(46.dp)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(primaryColor.copy(alpha = 0.72f)),
                        )
                        Spacer(Modifier.width(5.dp))
                        Box(
                            modifier =
                                Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.45f)),
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Surface(
                    onClick = onAvatarClick,
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shadowElevation = 1.dp,
                    modifier =
                        Modifier
                            .size(50.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                                RoundedCornerShape(14.dp),
                            ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        if (photoUri.isNullOrBlank()) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Abrir perfil",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp),
                            )
                        } else {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Abrir perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
            AirMailStripe(
                primaryColor = primaryColor.copy(alpha = 0.6f),
                secondaryColor = stripeAccentColor.copy(alpha = 0.55f),
            )
        }
    }
}

@Composable
private fun PostalStampMark(primaryColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(34.dp)
                    .rotate(-12f)
                    .border(
                        1.5.dp,
                        primaryColor.copy(alpha = 0.65f),
                        CircleShape,
                    )
                    .border(
                        3.dp,
                        primaryColor.copy(alpha = 0.18f),
                        CircleShape,
                    ),
        ) {
            Icon(
                imageVector = Icons.Outlined.FlightTakeoff,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.88f),
                modifier = Modifier.size(17.dp),
            )
        }
        Canvas(
            modifier =
                Modifier
                    .width(22.dp)
                    .height(20.dp),
        ) {
            val waveColor = primaryColor.copy(alpha = 0.42f)
            val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
            listOf(0.22f, 0.5f, 0.78f).forEach { yRatio ->
                val y = size.height * yRatio
                val path =
                    Path().apply {
                        moveTo(2.dp.toPx(), y)
                        quadraticTo(
                            size.width * 0.35f,
                            y - 2.2.dp.toPx(),
                            size.width * 0.6f,
                            y,
                        )
                        quadraticTo(
                            size.width * 0.82f,
                            y + 2.2.dp.toPx(),
                            size.width,
                            y,
                        )
                    }
                drawPath(path = path, color = waveColor, style = stroke)
            }
        }
    }
}

@Composable
private fun AirMailStripe(
    primaryColor: Color,
    secondaryColor: Color,
) {
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clipToBounds(),
    ) {
        val stripeWidth = 10.dp.toPx()
        val gap = 6.dp.toPx()
        val step = stripeWidth + gap
        val skew = size.height * 1.4f
        var x = -skew
        var index = 0
        while (x < size.width + skew) {
            val color = if (index % 2 == 0) primaryColor else secondaryColor
            val path =
                Path().apply {
                    moveTo(x, size.height)
                    lineTo(x + skew, 0f)
                    lineTo(x + skew + stripeWidth, 0f)
                    lineTo(x + stripeWidth, size.height)
                    close()
                }
            drawPath(path = path, color = color)
            x += step
            index++
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val barColor =
        if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        }
    val items =
        listOf(
            Triple("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
            Triple("Mapa", Icons.Filled.Map, Icons.Outlined.Map),
            Triple("Fotos", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
            Triple("Perfil", Icons.Filled.Person, Icons.Outlined.Person),
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = barColor,
            shadowElevation = 6.dp,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        RoundedCornerShape(26.dp),
                    ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                    val selected = selectedTab == index
                    val interactionSource = remember { MutableInteractionSource() }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier =
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onTabSelected(index) },
                                )
                                .padding(vertical = 4.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                                Modifier
                                    .width(54.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(
                                        if (selected) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                        } else {
                                            Color.Transparent
                                        },
                                    ),
                        ) {
                            Icon(
                                imageVector = if (selected) filledIcon else outlinedIcon,
                                contentDescription = label,
                                tint =
                                    if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f)
                                    },
                                modifier = Modifier.size(21.dp),
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = label,
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                ),
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f)
                                },
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier =
                                Modifier
                                    .width(if (selected) 14.dp else 0.dp)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(
                                        if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color.Transparent
                                        },
                                    ),
                        )
                    }
                }
            }
        }
    }
}
