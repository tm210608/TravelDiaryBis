package com.example.traveldiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
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
                Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp))
                Text("Pulsa para tomar una foto", modifier = Modifier.padding(top = 64.dp))
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

        OutlinedTextField(
            value = form.title,
            onValueChange = { onFormChange(form.copy(title = it)) },
            label = { Text("Título del viaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = form.location,
                onValueChange = { onFormChange(form.copy(location = it)) },
                label = { Text("Ubicación") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = form.country,
                onValueChange = { onFormChange(form.copy(country = it)) },
                label = { Text("País") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        OutlinedTextField(
            value = form.tag,
            onValueChange = { onFormChange(form.copy(tag = it)) },
            label = { Text("Etiqueta (ej. Senderismo, Ciudad)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it)) },
            label = { Text("Cuéntanos tu historia...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
        )

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Fecha: ${form.date.formatForEntryForm()}")
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
        .withLocale(Locale("es"))
        .withZone(ZoneId.systemDefault())
        .format(this)
