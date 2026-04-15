package com.example.traveldiary.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.traveldiary.model.TravelEntry
import com.example.traveldiary.model.allEntries
import com.example.traveldiary.model.featuredEntries
import com.example.traveldiary.model.filterChips
import com.example.traveldiary.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDiaryHomeScreen(
    onEntryClick: (TravelEntry) -> Unit = {},
    onAddClick: () -> Unit = {},
    viewModel: com.example.traveldiary.ui.viewmodel.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.example.traveldiary.ui.viewmodel.AppViewModelProvider.Factory
    )
) {
    val entriesList by viewModel.entriesList.collectAsState()
    val selectedChip by viewModel.selectedChip
    val selectedTab by viewModel.selectedTab

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nueva entrada",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp),
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Cabecera superior
            item { TopBar() }

            // Barra de búsqueda
            item {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            // Chips de filtrado
            item {
                FilterChipsRow(
                    chips = filterChips,
                    selectedChip = selectedChip,
                    onChipSelected = { viewModel.onChipSelected(it) },
                )
            }

            // Sección de destacados
            item {
                SectionHeader(
                    title = "Momentos Destacados",
                    actionLabel = "Ver todo",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            item {
                FeaturedEntriesRow(
                    entries = entriesList.filter { it.isFavourite }.take(3),
                    onEntryClick = onEntryClick,
                )
            }

            // Lista completa de entradas
            item {
                SectionHeader(
                    title = "Todos los Viajes",
                    actionLabel = "${entriesList.size} viajes",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            itemsIndexed(entriesList, key = { _, entry -> entry.id }) { index, entry ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 100L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(500)
                    )
                ) {
                    EntryListCard(
                        entry = entry,
                        onClick = { onEntryClick(entry) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                    )
                }
            }
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
