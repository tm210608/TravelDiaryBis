package com.example.traveldiary.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.ui.components.TravelEntryFormFields
import com.example.traveldiary.ui.viewmodel.EditEntryUiState
import com.example.traveldiary.ui.viewmodel.EditEntryViewModel
import com.example.traveldiary.ui.viewmodel.toTravelEntry
import com.example.traveldiary.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTravelEntryScreen(
    entryId: Int,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    onEntryNotFound: () -> Unit,
    onCameraClick: () -> Unit,
    viewModel: EditEntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDiscardConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is EditEntryUiState.Saved -> onSaved()
            is EditEntryUiState.NotFound -> onEntryNotFound()
            is EditEntryUiState.Editing -> state.error?.let { snackbarHostState.showSnackbar(it) }
            else -> Unit
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            coroutineScope.launch {
                val location = locationHelper.getFreshLocation()
                viewModel.saveWithUpdatedLocation(location?.latitude, location?.longitude)
            }
        } else {
            viewModel.locationUpdateFailed()
        }
    }

    fun requestBack() {
        val hasUnsavedChanges = when (val state = uiState) {
            is EditEntryUiState.Editing -> state.isDirty
            is EditEntryUiState.ConfirmingLocation -> state.form.toTravelEntry() != state.entry
            is EditEntryUiState.UpdatingLocation -> state.form.toTravelEntry() != state.entry
            is EditEntryUiState.Saving -> true
            else -> false
        }
        if (hasUnsavedChanges) showDiscardConfirmation = true else onBackClick()
    }

    fun updateLocation() {
        viewModel.requestLocationUpdate()
        if (LocationHelper.hasLocationPermissions(context)) {
            coroutineScope.launch {
                val location = locationHelper.getFreshLocation()
                viewModel.saveWithUpdatedLocation(location?.latitude, location?.longitude)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    BackHandler(enabled = uiState !is EditEntryUiState.Saving && uiState !is EditEntryUiState.UpdatingLocation) {
        requestBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar recuerdo") },
                navigationIcon = {
                    IconButton(
                        onClick = ::requestBack,
                        enabled = uiState !is EditEntryUiState.Saving && uiState !is EditEntryUiState.UpdatingLocation,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Button(
                        onClick = viewModel::onSaveClicked,
                        enabled = uiState is EditEntryUiState.Editing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            EditEntryUiState.Loading,
            is EditEntryUiState.Saving,
            is EditEntryUiState.UpdatingLocation -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is EditEntryUiState.Editing -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    TravelEntryFormFields(
                        form = state.form,
                        onFormChange = viewModel::updateForm,
                        onCameraClick = onCameraClick,
                        allowPhotoRemoval = state.form.imageUrl.isNotBlank(),
                        onRemovePhoto = viewModel::removeImage,
                    )
                    if (state.error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(state.error, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            is EditEntryUiState.ConfirmingLocation -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding))
            }
            is EditEntryUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadEntry(entryId) }) { Text("Reintentar") }
                }
            }
            EditEntryUiState.NotFound,
            is EditEntryUiState.Saved -> Unit
        }
    }

    if (uiState is EditEntryUiState.ConfirmingLocation) {
        val state = uiState as EditEntryUiState.ConfirmingLocation
        AlertDialog(
            onDismissRequest = viewModel::cancelLocationConfirmation,
            title = { Text("Confirmar ubicación") },
            text = {
                Text(
                    "¿La ubicación registrada sigue siendo correcta?\n" +
                        "${state.entry.latitude}, ${state.entry.longitude}"
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::keepLocationAndSave) { Text("Sí, conservar") }
            },
            dismissButton = {
                TextButton(onClick = ::updateLocation) { Text("No, actualizar ubicación") }
            },
        )
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Descartar cambios") },
            text = { Text("Tienes cambios sin guardar. ¿Quieres descartarlos?") },
            confirmButton = {
                TextButton(onClick = onBackClick) { Text("Descartar cambios") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) { Text("Seguir editando") }
            },
        )
    }
}
