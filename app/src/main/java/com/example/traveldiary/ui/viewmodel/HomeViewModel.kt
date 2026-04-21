package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.repository.TravelRepository
import com.example.traveldiary.domain.model.TravelEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TravelRepository) : ViewModel() {
    
    // Estado de la UI
    private val _selectedChip = mutableStateOf("All")
    val selectedChip: State<String> = _selectedChip

    private val _selectedTab = mutableIntStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    // Observar todas las entradas de la DB en tiempo real
    val entriesList: StateFlow<List<TravelEntry>> = repository.getAllEntriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onChipSelected(chip: String) {
        _selectedChip.value = chip
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }
    
    fun insertSampleEntry(entry: TravelEntry) {
        viewModelScope.launch {
            repository.insertEntry(entry)
        }
    }
}
