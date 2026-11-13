package com.example.traveldiary.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.domain.model.PassportData
import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.model.formatKm
import com.example.traveldiary.ui.components.BottomNavigationBar
import com.example.traveldiary.ui.components.PassportStamp
import com.example.traveldiary.ui.components.PostalHeader
import com.example.traveldiary.ui.components.SectionHeader
import com.example.traveldiary.ui.theme.MonoStubMeta
import com.example.traveldiary.ui.theme.MonoTelemetry
import com.example.traveldiary.ui.theme.SerifCardTitle
import com.example.traveldiary.ui.theme.SerifScreenTitle
import com.example.traveldiary.ui.viewmodel.ImportUiState
import com.example.traveldiary.ui.viewmodel.ProfileUiState
import com.example.traveldiary.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onTabSelected: (Int) -> Unit = {},
    onExportClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val importState by viewModel.importState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let { viewModel.importDiary(it.toString()) }
        }

    LaunchedEffect(importState) {
        when (val state = importState) {
            is ImportUiState.Success -> {
                snackbarHostState.showSnackbar("${state.count} entradas importadas")
                viewModel.onImportStateConsumed()
            }
            is ImportUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.onImportStateConsumed()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 3,
                onTabSelected = onTabSelected,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileUiState.Success -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                    ) {
                        PostalHeader(
                            title = "Perfil",
                            photoUri = state.profile.photoUri,
                            onAvatarClick = onEditProfileClick,
                            titleStyle = SerifScreenTitle,
                        )
                        Spacer(Modifier.height(14.dp))
                        StatsCard(
                            stats = state.stats,
                            passport = state.passport,
                            name = state.profile.name,
                        )
                        Spacer(Modifier.height(18.dp))
                        SectionHeader(
                            title = "Visados y Sellos de Ruta",
                            actionLabel = "%02d".format(state.passport.stamps.size),
                            eyebrow = "ADUANAS",
                        )
                        Spacer(Modifier.height(10.dp))
                        if (state.passport.stamps.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    ),
                            ) {
                                Text(
                                    text = "COMPLETA TU PRIMER VISADO",
                                    style = MonoTelemetry,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 22.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                )
                            }
                        } else {
                            state.passport.stamps.chunked(2).forEach { row ->
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    row.forEach { stamp ->
                                        PassportStamp(
                                            stamp = stamp,
                                            ink =
                                                if (state.passport.stamps.indexOf(stamp) % 2 == 0) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.tertiary
                                                },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "%02d VISADOS REGISTRADOS".format(state.stats.countries),
                            style = MonoStubMeta,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(18.dp))
                        ExportImportCards(
                            importState = importState,
                            onExportClick = onExportClick,
                            onImportClick = {
                                if (importState !is ImportUiState.Importing) {
                                    importLauncher.launch(
                                        arrayOf(
                                            "application/zip",
                                            "application/x-zip-compressed",
                                            "application/octet-stream",
                                        ),
                                    )
                                }
                            },
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    stats: TravelStats,
    passport: PassportData,
    name: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = name.ifBlank { "Viajero" },
                style = SerifCardTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text =
                    "${stats.totalTrips} viajes · ${stats.favourites} favoritos · " +
                        "${stats.countries} países · ${stats.continents} continentes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatColumn(value = formatKm(passport.kmTotal), unit = "km", label = "recorridos")
                StatColumn(value = "%02d".format(passport.daysOnRoute), label = "días en ruta")
                StatColumn(value = "%02d".format(passport.legs), label = "tramos con GPS")
            }
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String,
    unit: String? = null,
) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (unit != null) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp),
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ExportImportCards(
    importState: ImportUiState,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Card(
        onClick = onExportClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.FileUpload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Exportar diario",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "ZIP con entradas y fotos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    Card(
        onClick = onImportClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.FileDownload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Importar diario",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Restaura un ZIP exportado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (importState is ImportUiState.Importing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
