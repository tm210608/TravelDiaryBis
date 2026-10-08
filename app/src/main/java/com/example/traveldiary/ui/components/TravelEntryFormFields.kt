package com.example.traveldiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.traveldiary.ui.viewmodel.EntryDetails
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelEntryFormFields(
    form: EntryDetails,
    onFormChange: (EntryDetails) -> Unit,
    onCameraClick: () -> Unit,
    allowPhotoRemoval: Boolean,
    onRemovePhoto: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val hasPhoto = form.imageUrl.isNotBlank() && (allowPhotoRemoval || !form.imageUrl.startsWith("http"))
    val fieldShape = RoundedCornerShape(16.dp)
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    val dashedStroke = MaterialTheme.colorScheme.outlineVariant
    val cornerRadius = 24.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .drawBehind {
                    if (!hasPhoto) {
                        val stroke = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f)),
                        )
                        drawRoundRect(
                            color = dashedStroke,
                            style = stroke,
                            cornerRadius = CornerRadius(cornerRadius.toPx()),
                        )
                    }
                }
                .clickable(onClick = onCameraClick),
            contentAlignment = Alignment.Center,
        ) {
            if (hasPhoto) {
                AsyncImage(
                    model = form.imageUrl,
                    contentDescription = "Foto del viaje",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Pulsa para tomar una foto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (allowPhotoRemoval && hasPhoto) {
                IconButton(
                    onClick = onRemovePhoto,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f)),
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Quitar foto",
                        tint = Color.White,
                    )
                }
            }
        }

        TextField(
            value = form.title,
            onValueChange = { onFormChange(form.copy(title = it)) },
            label = { Text("Título del viaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = fieldShape,
            colors = fieldColors,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = form.location,
                onValueChange = { onFormChange(form.copy(location = it)) },
                label = { Text("Ubicación") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = fieldShape,
                colors = fieldColors,
            )
            TextField(
                value = form.country,
                onValueChange = { onFormChange(form.copy(country = it)) },
                label = { Text("País") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = fieldShape,
                colors = fieldColors,
            )
        }

        TextField(
            value = form.tag,
            onValueChange = { onFormChange(form.copy(tag = it)) },
            label = { Text("Etiqueta (ej. Senderismo, Ciudad)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = fieldShape,
            colors = fieldColors,
        )

        TextField(
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it)) },
            label = { Text("Cuéntanos tu historia...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            shape = fieldShape,
            colors = fieldColors,
        )

        Surface(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = fieldShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Fecha: ${form.date.formatForEntryForm()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (form.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (form.isFavourite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(8.dp))
                Text("Favorito", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            }
            Switch(
                checked = form.isFavourite,
                onCheckedChange = { onFormChange(form.copy(isFavourite = it)) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedBorderColor = Color.Transparent,
                ),
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = form.date.toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                            onFormChange(form.copy(date = date))
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun Instant.formatForEntryForm(): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.forLanguageTag("es"))
        .withZone(ZoneId.systemDefault())
        .format(this)
