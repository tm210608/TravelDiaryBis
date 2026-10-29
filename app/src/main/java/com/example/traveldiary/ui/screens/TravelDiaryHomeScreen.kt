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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.domain.model.HomeFilter
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.ui.components.*
import com.example.traveldiary.ui.viewmodel.HomeListState
import com.example.traveldiary.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDiaryHomeScreen(
    onEntryClick: (TravelEntry) -> Unit = {},
    onAddClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
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
                onTabSelected = { index ->
                    if (index == 1) {
                        onMapClick()
                    } else {
                        viewModel.onTabSelected(index)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item { TopBar() }

            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            item {
                FilterChipsRow(
                    chips = HomeFilter.entries.map { it.label },
                    selectedChip = selectedFilter.label,
                    onChipSelected = { label ->
                        viewModel.onFilterSelected(HomeFilter.entries.first { it.label == label })
                    },
                )
            }

            when (val state = listState) {
                is HomeListState.NoResults -> item {
                    NoResultsMessage(
                        state = state,
                        onShowAll = { viewModel.onShowAll() },
                    )
                }

                is HomeListState.Results -> {
                    item {
                        SectionHeader(
                            title = "Momentos Destacados",
                            actionLabel = "Ver todo",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }

                    item {
                        FeaturedEntriesRow(
                            entries = state.featured,
                            onEntryClick = onEntryClick,
                        )
                    }

                    item {
                        SectionHeader(
                            title = "Todos los Viajes",
                            actionLabel = "${state.entries.size} viajes",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }

                    itemsIndexed(state.entries, key = { _, entry -> entry.id }) { index, entry ->
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
    }
}

@Composable
private fun NoResultsMessage(
    state: HomeListState.NoResults,
    onShowAll: () -> Unit,
) {
    val message = if (state.query.isNotBlank()) {
        "No hay resultados para \"${state.query}\""
    } else {
        "No hay viajes en ${state.filter.label}"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .testTag("home_no_results"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onShowAll) {
            Text("Ver todos")
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
