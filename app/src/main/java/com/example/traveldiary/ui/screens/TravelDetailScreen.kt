package com.example.traveldiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.traveldiary.model.allEntries
import com.example.traveldiary.model.featuredEntries

@Composable
fun TravelDetailScreen(
    entryId: Int,
    onBackClick: () -> Unit
) {
    val entry = (allEntries + featuredEntries).find { it.id == entryId } ?: return
    var isFavourite by remember { mutableStateOf(entry.isFavourite) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Contenido Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen de Cabecera
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
                // Degradado para que el texto sobre la imagen se lea bien
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 600f
                            )
                        )
                )
                
                // Título y Ubicación sobre la imagen
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = entry.tag,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
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

            // Cuerpo de la noticia/viaje
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Info rápida (Fecha y Lugar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoChip(icon = Icons.Default.CalendarToday, text = entry.date)
                    InfoChip(icon = Icons.Default.LocationOn, text = "${entry.location}, ${entry.country}")
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

                if (entry.latitude != null && entry.longitude != null) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Placeholder decorativo del mapa
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                Text("Vista de Mapa: ${String.format("%.4f", entry.latitude)}, ${String.format("%.4f", entry.longitude)}")
                                Text("Google Maps configurado", style = MaterialTheme.typography.labelSmall)
                             }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                
                // Tarjeta de "Estadísticas" o Curiosidad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp)
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
                
                Spacer(modifier = Modifier.height(100.dp)) // Espacio final
            }
        }

        // Botones Flotantes Superiores (Back y Favourite)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GlassActionButton(
                icon = Icons.Default.ArrowBack,
                onClick = onBackClick
            )
            GlassActionButton(
                icon = if (isFavourite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                tint = if (isFavourite) Color(0xFFEF5350) else Color.White,
                onClick = { isFavourite = !isFavourite }
            )
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GlassActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            contentDescription = null,
            tint = tint
        )
    }
}
