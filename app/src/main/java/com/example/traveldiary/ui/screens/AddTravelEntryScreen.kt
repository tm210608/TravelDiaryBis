package com.example.traveldiary.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.traveldiary.ui.viewmodel.AddEntryViewModel
import com.example.traveldiary.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelEntryScreen(
    navigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    viewModel: AddEntryViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var hasLocationPermission by remember {
        mutableStateOf(LocationHelper.hasLocationPermissions(context))
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recuerdo") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atr\u00e1s")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (!hasLocationPermission) {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                                val location = if (hasLocationPermission) {
                                    locationHelper.getCurrentLocation()
                                } else null
                                viewModel.saveEntry(
                                    latitude = location?.latitude,
                                    longitude = location?.longitude
                                )
                                snackbarHostState.showSnackbar("Entrada guardada correctamente")
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onCameraClick() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.entryDetails.imageUrl.startsWith("http")) {
                    Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text("Pulsa para tomar una foto", modifier = Modifier.padding(top = 64.dp))
                } else {
                    AsyncImage(
                        model = uiState.entryDetails.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            OutlinedTextField(
                value = uiState.entryDetails.title,
                onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(title = it)) },
                label = { Text("T\u00edtulo del Viaje") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.entryDetails.location,
                    onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(location = it)) },
                    label = { Text("Ubicaci\u00f3n") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.entryDetails.country,
                    onValueChange = { viewModel.updateUiState(uiState.entryDetails.copy(country = it)) },
                    label = { Text("Pa\u00eds") },
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
                label = { Text("Cu\u00e9ntanos tu historia...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
            )

            Text(
                text = "La foto y las coordenadas GPS se a\u00f1adir\u00e1n autom\u00e1ticamente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
