package com.example.traveldiary.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.traveldiary.ui.components.TravelEntryFormFields
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
        ) {
            TravelEntryFormFields(
                form = uiState.entryDetails,
                onFormChange = viewModel::updateUiState,
                onCameraClick = onCameraClick,
                allowPhotoRemoval = false,
                onRemovePhoto = {},
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Las coordenadas GPS se añadirán automáticamente si están disponibles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
