package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.BuildConfig
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.model.shareMimeType
import com.example.traveldiary.domain.usecase.ExportEntriesUseCase
import com.example.traveldiary.domain.usecase.ExportPdfUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import com.example.traveldiary.domain.usecase.GetProfileUseCase
import com.example.traveldiary.domain.usecase.ToggleFavouriteUseCase
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

    data class ShareReady(val fileName: String, val mimeType: String) : ExportUiState

    data class Success(val fileName: String) : ExportUiState

    data class Error(val message: String) : ExportUiState
}

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getEntriesUseCase: GetEntriesUseCase,
        private val exportEntriesUseCase: ExportEntriesUseCase,
        private val exportPdfUseCase: ExportPdfUseCase,
        private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
        getProfileUseCase: GetProfileUseCase,
    ) : ViewModel() {
        val profile: StateFlow<UserProfile> =
            getProfileUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        private val _selectedTab = mutableIntStateOf(0)
        val selectedTab: State<Int> = _selectedTab

        private val _favouritesOnly = MutableStateFlow(false)
        val favouritesOnly: StateFlow<Boolean> = _favouritesOnly

        val entriesList: StateFlow<List<TravelEntry>> =
            combine(
                getEntriesUseCase(),
                _favouritesOnly,
            ) { entries, favOnly ->
                if (favOnly) entries.filter { it.isFavourite } else entries
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun onTabSelected(index: Int) {
            _selectedTab.value = index
        }

        fun onFavouritesOnlyChanged(favouritesOnly: Boolean) {
            _favouritesOnly.value = favouritesOnly
        }

        fun toggleFavourite(
            id: Int,
            current: Boolean,
        ) {
            viewModelScope.launch {
                toggleFavouriteUseCase(id, current)
            }
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

        fun exportSelectedTo(
            destinationUri: String,
            suggestedName: String,
        ) {
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
                    _exportState.value = ExportUiState.ShareReady(file.name, shareMimeType(file.name))
                    exitSelectionMode()
                } catch (e: Exception) {
                    _exportState.value = ExportUiState.Error(e.message ?: "Error al exportar")
                }
            }
        }

        fun exportPdfSelectedTo(
            destinationUri: String,
            suggestedName: String,
        ) {
            val ids = _selectedIds.value
            if (ids.isEmpty()) return
            _exportState.value = ExportUiState.Exporting
            viewModelScope.launch {
                try {
                    exportPdfUseCase.saveToUri(ids, destinationUri)
                    _exportState.value = ExportUiState.Success(suggestedName)
                    exitSelectionMode()
                } catch (e: Exception) {
                    _exportState.value = ExportUiState.Error(e.message ?: "Error al exportar")
                }
            }
        }

        fun sharePdfSelected() {
            val ids = _selectedIds.value
            if (ids.isEmpty()) return
            _exportState.value = ExportUiState.Exporting
            viewModelScope.launch {
                try {
                    val file = exportPdfUseCase.toTempPdf(ids)
                    _exportState.value = ExportUiState.ShareReady(file.name, shareMimeType(file.name))
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
