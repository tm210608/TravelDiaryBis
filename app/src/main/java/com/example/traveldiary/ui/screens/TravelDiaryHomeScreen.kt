package com.example.traveldiary.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.exportFileName
import com.example.traveldiary.model.filterChips
import com.example.traveldiary.ui.components.*
import com.example.traveldiary.ui.viewmodel.ExportUiState
import com.example.traveldiary.ui.viewmodel.HomeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDiaryHomeScreen(
    onEntryClick: (TravelEntry) -> Unit = {},
    onAddClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onMemoriesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    exportRequested: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val entriesList by viewModel.entriesList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedChip by viewModel.selectedChip.collectAsState()
    val selectedTab by viewModel.selectedTab
    val hasEntries by viewModel.hasEntries.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var suggestedName by remember { mutableStateOf("") }

    val createDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { viewModel.exportSelectedTo(it.toString(), suggestedName) }
    }

    LaunchedEffect(exportRequested) {
        if (exportRequested) viewModel.enterSelectionMode()
    }

    BackHandler(enabled = selectionMode) {
        viewModel.exitSelectionMode()
    }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportUiState.ShareReady -> {
                val file = File(File(context.cacheDir, "exports"), state.fileName)
                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir diario"))
                viewModel.onExportStateConsumed()
            }
            is ExportUiState.Success -> {
                snackbarHostState.showSnackbar("Diario exportado")
                viewModel.onExportStateConsumed()
            }
            is ExportUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.onExportStateConsumed()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!selectionMode) {
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(64.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Nueva entrada",
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    when (index) {
                        1 -> onMapClick()
                        2 -> onMemoriesClick()
                        3 -> onProfileClick()
                        else -> viewModel.onTabSelected(index)
                    }
                },
            )
        },
    ) { innerPadding ->
        val gridColor = MaterialTheme.colorScheme.onSurface.copy(
            alpha = if (isSystemInDarkTheme()) 0.12f else 0.14f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .cartographyGrid(gridColor)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                if (selectionMode) {
                    SelectionTopBar(
                        count = selectedIds.size,
                        exporting = exportState is ExportUiState.Exporting,
                        onClose = { viewModel.exitSelectionMode() },
                        onSelectAll = { viewModel.selectAll() },
                        onSave = {
                            suggestedName = exportFileName()
                            createDocument.launch(suggestedName)
                        },
                        onShare = { viewModel.shareSelected() },
                    )
                } else {
                    TopBar(
                        showExport = hasEntries,
                        onExportClick = { viewModel.enterSelectionMode() },
                    )
                }
            }

            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            item {
                FilterChipsRow(
                    chips = filterChips,
                    selectedChip = selectedChip,
                    onChipSelected = { viewModel.onChipSelected(it) },
                )
            }

            val featured = entriesList.filter { it.isFavourite }.take(3)
                .ifEmpty { entriesList.take(3) }
            if (featured.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Registro Destacado",
                        actionLabel = "%02d".format(featured.size),
                        eyebrow = "EXPEDICIÓN",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }

                item {
                    FeaturedEntriesRow(
                        entries = featured,
                        onEntryClick = onEntryClick,
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Todos los Viajes",
                    actionLabel = "%02d".format(entriesList.size),
                    eyebrow = "HOJA DE RUTA",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            items(entriesList, key = { it.id }) { entry ->
                EntryListCard(
                    entry = entry,
                    selectionMode = selectionMode,
                    selected = entry.id in selectedIds,
                    onClick = {
                        if (selectionMode) viewModel.toggleSelection(entry.id)
                        else onEntryClick(entry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .animateItem(),
                )
            }
        }
        }
    }
}

@Composable
private fun SelectionTopBar(
    count: Int,
    exporting: Boolean,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
) {
    var exportMenuExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar selección",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "$count seleccionadas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onSelectAll, enabled = !exporting) {
                    Text("Todo")
                }
                Box {
                    TextButton(
                        onClick = { exportMenuExpanded = true },
                        enabled = count > 0 && !exporting,
                    ) {
                        Text("Exportar")
                    }
                    DropdownMenu(
                        expanded = exportMenuExpanded,
                        onDismissRequest = { exportMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Guardar en dispositivo") },
                            onClick = {
                                exportMenuExpanded = false
                                onSave()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir") },
                            onClick = {
                                exportMenuExpanded = false
                                onShare()
                            },
                        )
                    }
                }
            }
        }
        if (exporting) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TravelDiaryHomeScreenPreview() {
    MaterialTheme {
        TravelDiaryHomeScreen()
    }
}
