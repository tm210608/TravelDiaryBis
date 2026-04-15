package com.example.traveldiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.traveldiary.ui.viewmodel.AddEntryViewModel
import com.example.traveldiary.ui.viewmodel.AppViewModelProvider
import com.example.traveldiary.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelEntryScreen(
    navigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    viewModel: AddEntryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recuerdo") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val location = locationHelper.getCurrentLocation()
                                viewModel.saveEntry(
                                    latitude = location?.latitude,
                                    longitude = location?.longitude
                                )
                                navigateBack()
                            }
                        },
                        enabled = uiState.isEntryValid,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Previsualización de Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onCameraClick() },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (uiState.entryDetails.imageUrl.startsWith("http")) {
                   Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(48.dp))
                   Text("Pulsa para tomar una foto", modifier = Modifier.padding(top = 64.dp))
                } else {
                    coil.compose.AsyncImage(
                        model = uiState.entryDetails.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            OutlinedTextField(
                value = uiState.entryDetails.title,
                onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(title = it)) },
                label = { Text("Título del Viaje") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.entryDetails.location,
                    onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(location = it)) },
                    label = { Text("Ubicación") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.entryDetails.country,
                    onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(country = it)) },
                    label = { Text("País") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = uiState.entryDetails.tag,
                onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(tag = it)) },
                label = { Text("Etiqueta (ej. Senderismo, Ciudad)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.entryDetails.description,
                onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(description = it)) },
                label = { Text("Cuéntanos tu historia...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
            )
            
            Text(
                text = "La foto y las coordenadas GPS se añadirán automáticamente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
