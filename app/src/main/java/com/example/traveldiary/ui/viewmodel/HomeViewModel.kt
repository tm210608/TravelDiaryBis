package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.BuildConfig
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.ExportEntriesUseCase
import com.example.traveldiary.domain.usecase.FilterEntriesByChipUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExportUiState {
    data object Idle : ExportUiState
    data object Exporting : ExportUiState
    data class ShareReady(val fileName: String) : ExportUiState
    data class Success(val fileName: String) : ExportUiState
    data class Error(val message: String) : ExportUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getEntriesUseCase: GetEntriesUseCase,
    private val filterEntriesByChipUseCase: FilterEntriesByChipUseCase,
    private val exportEntriesUseCase: ExportEntriesUseCase,
) : ViewModel() {

    private val _selectedChip = MutableStateFlow(FilterEntriesByChipUseCase.CHIP_TODO)
    val selectedChip: StateFlow<String> = _selectedChip

    private val _selectedTab = mutableIntStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allEntries: StateFlow<List<TravelEntry>> = getEntriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entriesList: StateFlow<List<TravelEntry>> = combine(
        _allEntries, _searchQuery, _selectedChip
    ) { entries, query, chip ->
        val byChip = filterEntriesByChipUseCase(entries, chip)
        if (query.isBlank()) byChip
        else byChip.filter {
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

    private val _selectionMode = MutableStateFlow(false)
    val selectionMode: StateFlow<Boolean> = _selectionMode

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds

    private val _exportState = MutableStateFlow<ExportUiState>(ExportUiState.Idle)
    val exportState: StateFlow<ExportUiState> = _exportState

    fun enterSelectionMode() {
        _selectionMode.value = true
    }

    fun exitSelectionMode() {
        _selectionMode.value = false
        _selectedIds.value = emptySet()
    }

    fun toggleSelection(id: Int) {
        _selectedIds.value = _selectedIds.value.let { if (id in it) it - id else it + id }
    }

    fun selectAll() {
        _selectedIds.value = entriesList.value.map { it.id }.toSet()
    }

    fun exportSelectedTo(destinationUri: String, suggestedName: String) {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        _exportState.value = ExportUiState.Exporting
        viewModelScope.launch {
            try {
                exportEntriesUseCase.saveToUri(ids, destinationUri, BuildConfig.VERSION_NAME)
                _exportState.value = ExportUiState.Success(suggestedName)
                exitSelectionMode()
            } catch (e: Exception) {
                _exportState.value = ExportUiState.Error(e.message ?: "Error al exportar")
            }
        }
    }

    fun shareSelected() {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        _exportState.value = ExportUiState.Exporting
        viewModelScope.launch {
            try {
                val file = exportEntriesUseCase.toTempZip(ids, BuildConfig.VERSION_NAME)
                _exportState.value = ExportUiState.ShareReady(file.name)
                exitSelectionMode()
            } catch (e: Exception) {
                _exportState.value = ExportUiState.Error(e.message ?: "Error al exportar")
            }
        }
    }

    fun onExportStateConsumed() {
        _exportState.value = ExportUiState.Idle
    }
}
