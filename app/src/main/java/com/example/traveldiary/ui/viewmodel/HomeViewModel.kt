package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.data.TravelRepository
import com.example.traveldiary.model.TravelEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TravelRepository
) : ViewModel() {

    private val _selectedChip = mutableStateOf("Todo")
    val selectedChip: State<String> = _selectedChip

    private val _selectedTab = mutableIntStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allEntries: StateFlow<List<TravelEntry>> = repository.getAllEntriesStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entriesList: StateFlow<List<TravelEntry>> = combine(
        _allEntries, _searchQuery
    ) { entries, query ->
        if (query.isBlank()) entries
        else entries.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.location.contains(query, ignoreCase = true) ||
            it.country.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onChipSelected(chip: String) {
        _selectedChip.value = chip
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun insertSampleEntry(entry: TravelEntry) {
        viewModelScope.launch {
            repository.insertEntry(entry)
        }
    }
}
