package com.example.traveldiary.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.traveldiary.domain.model.formatToTicket
import com.example.traveldiary.domain.model.toCardinalLat
import com.example.traveldiary.domain.model.toCardinalLon
import com.example.traveldiary.ui.components.TicketDivider
import com.example.traveldiary.ui.components.cartographyGrid
import com.example.traveldiary.ui.components.ticketShape
import com.example.traveldiary.ui.theme.MonoEyebrow
import com.example.traveldiary.ui.theme.MonoStubMeta
import com.example.traveldiary.ui.theme.MonoTelemetry
import com.example.traveldiary.ui.viewmodel.DetailUiState
import com.example.traveldiary.ui.viewmodel.DetailViewModel

@Composable
fun TravelDetailScreen(
    entryId: Int,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit = {},
    refreshKey: Int = 0,
    viewModel: DetailViewModel = hiltViewModel()
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
                entry = com.example.traveldiary.ui.viewmodel.EntryDetails(
                    id = entry.id,
                    title = entry.title,
                    location = entry.location,
                    country = entry.country,
                    date = entry.date,
                    tag = entry.tag,
                    description = entry.description,
                    imageUrl = entry.imageUrl,
                    latitude = entry.latitude,
                    longitude = entry.longitude,
                    isFavourite = entry.isFavourite
                ),
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onFavouriteClick = { viewModel.toggleFavourite(entry.id, entry.isFavourite) }
            )
        }
    }
}

@Composable
private fun TravelDetailContent(
    entry: com.example.traveldiary.ui.viewmodel.EntryDetails,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    val context = LocalContext.current
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (isSystemInDarkTheme()) 0.12f else 0.14f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .cartographyGrid(gridColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                AsyncImage(
                    model = entry.imageUrl,
                    contentDescription = entry.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f)),
                                startY = 600f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = entry.tag,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                val ticket = entry.date.formatToTicket()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (entry.latitude != null && entry.longitude != null) {
                                Modifier.clickable {
                                    val gmmIntentUri = Uri.parse("geo:${entry.latitude},${entry.longitude}?q=${entry.latitude},${entry.longitude}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(mapIntent)
                                    }
                                }
                            } else Modifier
                        ),
                    shape = ticketShape(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                        ) {
                            Text(
                                text = "TALÓN DE EXPEDICIÓN",
                                style = MonoEyebrow,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${ticket.day} ${ticket.month} ${ticket.year} · ${ticket.time}",
                                style = MonoTelemetry,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "${entry.location}, ${entry.country}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        TicketDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.height(52.dp),
                        )
                        Column(
                            modifier = Modifier.width(92.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (entry.latitude != null && entry.longitude != null) {
                                Text(
                                    text = entry.latitude.toCardinalLat(),
                                    style = MonoTelemetry,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                Text(
                                    text = entry.longitude.toCardinalLon(),
                                    style = MonoTelemetry,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "VER MAPA",
                                    style = MonoStubMeta,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                Text(
                                    text = "SIN GPS",
                                    style = MonoTelemetry,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "La Historia",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Comparte este recuerdo con tus amigos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GlassActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                onClick = onBackClick
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassActionButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Editar entrada",
                    onClick = onEditClick
                )
                GlassActionButton(
                    icon = if (entry.isFavourite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (entry.isFavourite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (entry.isFavourite) MaterialTheme.colorScheme.error else Color.White,
                    onClick = onFavouriteClick
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
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
