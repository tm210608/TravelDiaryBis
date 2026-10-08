package com.example.traveldiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.model.filterChips
import com.example.traveldiary.ui.components.*
import com.example.traveldiary.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDiaryHomeScreen(
    onEntryClick: (TravelEntry) -> Unit = {},
    onAddClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onMemoriesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val entriesList by viewModel.entriesList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedChip by viewModel.selectedChip.collectAsState()
    val selectedTab by viewModel.selectedTab

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
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

            item {
                SectionHeader(
                    title = "Momentos Destacados",
                    actionLabel = "Ver todo",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            item {
                FeaturedEntriesRow(
                    entries = entriesList.filter { it.isFavourite }.take(3),
                    onEntryClick = onEntryClick,
                )
            }

            item {
                SectionHeader(
                    title = "Todos los Viajes",
                    actionLabel = "${entriesList.size} viajes",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            items(entriesList, key = { it.id }) { entry ->
                EntryListCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .animateItem(),
                )
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
