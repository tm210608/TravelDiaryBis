package com.example.traveldiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.ui.components.BottomNavigationBar
import com.example.traveldiary.ui.theme.MonoEyebrow
import com.example.traveldiary.ui.viewmodel.MapUiState
import com.example.traveldiary.ui.viewmodel.MapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onEntryClick: (Int) -> Unit,
    onTabSelected: (Int) -> Unit = {},
    onAddClick: () -> Unit = {},
    showBottomBar: Boolean = false,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedTab = 1,
                    onTabSelected = onTabSelected,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
        ) {
            when (val state = uiState) {
                is MapUiState.Loading -> LoadingState()
                is MapUiState.Empty -> EmptyState(onAddClick = onAddClick)
                is MapUiState.Success ->
                    MapContent(
                        entries = state.entries,
                        onEntryClick = onEntryClick,
                    )
            }

            Surface(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f),
                shadowElevation = 4.dp,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp)) {
                        Text(
                            text = "CARTOGRAFÍA",
                            style = MonoEyebrow,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Mapa de Viajes",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
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
private fun EmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Aún no tienes viajes con ubicación",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Añade una entrada para verla aquí.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onAddClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Crear tu primer viaje")
        }
    }
}

@Composable
private fun MapContent(
    entries: List<TravelEntry>,
    onEntryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val routeColor = MaterialTheme.colorScheme.primary
    val primaryArgb =
        android.graphics.Color.argb(
            255,
            (routeColor.red * 255).toInt(),
            (routeColor.green * 255).toInt(),
            (routeColor.blue * 255).toInt(),
        )

    val geoPoints =
        remember(entries) {
            entries
                .filter { it.latitude != null && it.longitude != null }
                .sortedBy { it.date }
                .map { GeoPoint(it.latitude!!, it.longitude!!) }
        }

    val copyrightOverlay = remember { CopyrightOverlay(context) }

    val mapView =
        remember {
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                if (isProbablyEmulator()) {
                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                }
            }
        }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    else -> Unit
                }
            }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            if (map.tag == entries) return@AndroidView
            map.tag = entries
            map.overlays.clear()
            map.overlays.add(copyrightOverlay)

            if (geoPoints.size >= 2) {
                val polyline =
                    Polyline().apply {
                        setPoints(geoPoints)
                        outlinePaint.color = primaryArgb
                        outlinePaint.strokeWidth = 8f
                    }
                map.overlays.add(polyline)
            }

            entries.forEach { entry ->
                if (entry.latitude != null && entry.longitude != null) {
                    val marker =
                        Marker(map).apply {
                            position = GeoPoint(entry.latitude!!, entry.longitude!!)
                            title = entry.title
                            snippet = entry.location
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                onEntryClick(entry.id)
                                true
                            }
                        }
                    map.overlays.add(marker)
                }
            }

            when {
                geoPoints.size == 1 -> {
                    map.controller.setZoom(12.0)
                    map.controller.setCenter(geoPoints.first())
                }
                geoPoints.size > 1 -> {
                    val box = BoundingBox.fromGeoPoints(geoPoints)
                    map.post { map.zoomToBoundingBox(box, false, 120) }
                }
            }
            map.invalidate()
        },
    )
}

private fun isProbablyEmulator(): Boolean =
    android.os.Build.FINGERPRINT.startsWith("generic") ||
        android.os.Build.FINGERPRINT.contains("emulator") ||
        android.os.Build.MODEL.contains("Emulator") ||
        android.os.Build.HARDWARE in setOf("goldfish", "ranchu", "vbox86")
