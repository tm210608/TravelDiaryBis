package com.example.traveldiary.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.model.exportFileName
import com.example.traveldiary.domain.model.exportPdfFileName
import com.example.traveldiary.ui.components.BottomNavigationBar
import com.example.traveldiary.ui.components.PostalEntryCard
import com.example.traveldiary.ui.theme.SerifAppTitle
import com.example.traveldiary.ui.theme.SerifCardTitle
import com.example.traveldiary.ui.viewmodel.ExportUiState
import com.example.traveldiary.ui.viewmodel.HomeViewModel
import java.io.File

@Composable
fun TravelDiaryHomeScreen(
    onEntryClick: (TravelEntry) -> Unit = {},
    onAddClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onMemoriesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    exportRequested: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val entriesList by viewModel.entriesList.collectAsState()
    val selectedTab by viewModel.selectedTab
    val profile by viewModel.profile.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var suggestedName by remember { mutableStateOf("") }

    val createDocument =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/zip"),
        ) { uri ->
            uri?.let { viewModel.exportSelectedTo(it.toString(), suggestedName) }
        }

    val createPdfDocument =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/pdf"),
        ) { uri ->
            uri?.let { viewModel.exportPdfSelectedTo(it.toString(), suggestedName) }
        }

    var exportHandled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(exportRequested) {
        if (exportRequested && !exportHandled) {
            viewModel.enterSelectionMode()
            exportHandled = true
        }
    }

    BackHandler(enabled = selectionMode) {
        viewModel.exitSelectionMode()
    }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportUiState.ShareReady -> {
                val file = File(File(context.cacheDir, "exports"), state.fileName)
                val uri =
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file,
                    )
                val intent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = state.mimeType
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
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Nueva entrada")
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
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(bottom = 110.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (selectionMode) {
                    SelectionTopBar(
                        count = selectedIds.size,
                        exporting = exportState is ExportUiState.Exporting,
                        onClose = { viewModel.exitSelectionMode() },
                        onSelectAll = { viewModel.selectAll() },
                        onSaveZip = {
                            suggestedName = exportFileName()
                            createDocument.launch(suggestedName)
                        },
                        onSavePdf = {
                            suggestedName = exportPdfFileName()
                            createPdfDocument.launch(suggestedName)
                        },
                        onShareZip = { viewModel.shareSelected() },
                        onSharePdf = { viewModel.sharePdfSelected() },
                    )
                } else {
                    HomeHeader(
                        profile = profile,
                        onAvatarClick = onAvatarClick,
                    )
                }
            }

            if (!selectionMode && entriesList.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(onAddClick = onAddClick)
                }
            } else {
                items(entriesList, key = { it.id }) { entry ->
                    PostalEntryCard(
                        entry = entry,
                        selectionMode = selectionMode,
                        selected = entry.id in selectedIds,
                        onClick = {
                            if (selectionMode) {
                                viewModel.toggleSelection(entry.id)
                            } else {
                                onEntryClick(entry)
                            }
                        },
                        onFavouriteClick = {
                            viewModel.toggleFavourite(entry.id, entry.isFavourite)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    profile: UserProfile,
    onAvatarClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 6.dp),
    ) {
        Text(
            text = "Mi Diario",
            style = SerifAppTitle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Surface(
            onClick = onAvatarClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(46.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (profile.photoUri.isNullOrBlank()) {
                    Text(
                        text = profile.initials,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    AsyncImage(
                        model = profile.photoUri,
                        contentDescription = "Abrir perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
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
    onSaveZip: () -> Unit,
    onSavePdf: () -> Unit,
    onShareZip: () -> Unit,
    onSharePdf: () -> Unit,
) {
    var exportMenuExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier =
                Modifier
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
                            text = { Text("Guardar como ZIP") },
                            onClick = {
                                exportMenuExpanded = false
                                onSaveZip()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Guardar como PDF") },
                            onClick = {
                                exportMenuExpanded = false
                                onSavePdf()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir ZIP") },
                            onClick = {
                                exportMenuExpanded = false
                                onShareZip()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir PDF") },
                            onClick = {
                                exportMenuExpanded = false
                                onSharePdf()
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

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.size(72.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Tu diario está en blanco",
            style = SerifCardTitle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Guarda tu primer viaje con fotos y lugar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onAddClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            Text("Crear tu primer viaje")
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
