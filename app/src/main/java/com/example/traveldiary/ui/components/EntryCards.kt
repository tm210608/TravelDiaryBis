package com.example.traveldiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.formatToDisplay
import com.example.traveldiary.ui.theme.SerifCardTitle

@Composable
fun PostalEntryCard(
    entry: TravelEntry,
    onClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    selectionMode: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val photoShape = RoundedCornerShape(12.dp)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 5.dp,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp)) {
            Box {
                if (entry.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.imageUrl,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(photoShape)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    photoShape,
                                ),
                    )
                } else {
                    PostalPhotoPlaceholder(shape = photoShape)
                }
                if (selectionMode) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                    ) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = { onClick() },
                        )
                    }
                } else {
                    PostalHeart(
                        isFavourite = entry.isFavourite,
                        onClick = onFavouriteClick,
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    )
                }
            }
            Column(Modifier.padding(horizontal = 4.dp).padding(top = 10.dp)) {
                Text(
                    text = entry.title,
                    style = SerifCardTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        listOf(entry.location, entry.country)
                            .filter { it.isNotBlank() }
                            .joinToString(", ") +
                            " · " +
                            entry.date.formatToDisplay(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun PostalHeart(
    isFavourite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 3.dp,
        modifier = modifier.size(40.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavourite) "Quitar de favoritos" else "Marcar como favorito",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun PostalPhotoPlaceholder(
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        ),
                    ),
                ),
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            modifier = Modifier.size(44.dp),
        )
    }
}
