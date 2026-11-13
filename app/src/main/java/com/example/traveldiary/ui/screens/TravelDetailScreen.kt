package com.example.traveldiary.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.traveldiary.domain.model.formatToDisplay
import com.example.traveldiary.ui.theme.SerifCardTitle
import com.example.traveldiary.ui.theme.SerifScreenTitle
import com.example.traveldiary.ui.viewmodel.DetailUiState
import com.example.traveldiary.ui.viewmodel.DetailViewModel

@Composable
fun TravelDetailScreen(
    entryId: Int,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit = {},
    refreshKey: Int = 0,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryId, refreshKey) {
        viewModel.loadEntry(entryId)
    }

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadEntry(entryId) }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        is DetailUiState.Success -> {
            val entry = state.entry
            TravelDetailContent(
                entry =
                    com.example.traveldiary.ui.viewmodel.EntryDetails(
                        id = entry.id,
                        title = entry.title,
                        location = entry.location,
                        country = entry.country,
                        date = entry.date,
                        tag = entry.tag,
                        description = entry.description,
                        imageUrl = entry.imageUrl,
                        photoUris = entry.photoUris,
                        latitude = entry.latitude,
                        longitude = entry.longitude,
                        isFavourite = entry.isFavourite,
                    ),
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onFavouriteClick = { viewModel.toggleFavourite(entry.id, entry.isFavourite) },
            )
        }
    }
}

@Composable
private fun TravelDetailContent(
    entry: com.example.traveldiary.ui.viewmodel.EntryDetails,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFavouriteClick: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp),
            ) {
                if (entry.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.imageUrl,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        ),
                                    ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            modifier = Modifier.size(56.dp),
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
            ) {
                Text(
                    text = entry.title,
                    style = SerifScreenTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text =
                        listOf(entry.location, entry.country)
                            .filter { it.isNotBlank() }
                            .joinToString(", ") +
                            " · " +
                            entry.date.formatToDisplay(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (entry.latitude != null && entry.longitude != null) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val gmmIntentUri =
                                Uri.parse(
                                    "geo:${entry.latitude},${entry.longitude}?q=${entry.latitude},${entry.longitude}",
                                )
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ver mapa",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                if (entry.photoUris.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(entry.photoUris) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto del recuerdo",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .size(140.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                    }
                }

                if (entry.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "La Historia",
                        style = SerifCardTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = entry.description,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 28.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Comparte este recuerdo con tus amigos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GlassActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                onClick = onBackClick,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassActionButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Editar entrada",
                    onClick = onEditClick,
                )
                GlassActionButton(
                    icon = if (entry.isFavourite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (entry.isFavourite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (entry.isFavourite) MaterialTheme.colorScheme.error else Color.White,
                    onClick = onFavouriteClick,
                )
            }
        }
    }
}

@Composable
fun GlassActionButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = Color.White,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f)),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
