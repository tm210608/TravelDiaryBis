package com.example.traveldiary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.ui.viewmodel.MapUiState
import com.example.traveldiary.ui.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onEntryClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Viajes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is MapUiState.Loading -> LoadingState(modifier = Modifier.padding(innerPadding))
            is MapUiState.Empty -> EmptyState(modifier = Modifier.padding(innerPadding))
            is MapUiState.Success -> MapContent(
                entries = state.entries,
                onEntryClick = onEntryClick,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Aún no tienes viajes con ubicación",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Añade una entrada para verla aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MapContent(
    entries: List<TravelEntry>,
    onEntryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                entries.first().latitude ?: 0.0,
                entries.first().longitude ?: 0.0,
            ),
            10f,
        )
    }

    LaunchedEffect(entries) {
        if (entries.size == 1) {
            val entry = entries.first()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(entry.latitude ?: 0.0, entry.longitude ?: 0.0),
                    10f,
                ),
            )
        } else if (entries.size > 1) {
            val bounds = LatLngBounds.builder().apply {
                entries.forEach { entry ->
                    include(LatLng(entry.latitude ?: 0.0, entry.longitude ?: 0.0))
                }
            }.build()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100),
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        entries.forEach { entry ->
            key(entry.id) {
                val position = LatLng(entry.latitude ?: 0.0, entry.longitude ?: 0.0)
                val markerState = rememberMarkerState(position = position)
                Marker(
                    state = markerState,
                    title = entry.title,
                    snippet = entry.location,
                    onInfoWindowClick = {
                        onEntryClick(entry.id)
                    },
                )
            }
        }
    }
}
