package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.model.PassportData
import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.usecase.GetPassportUseCase
import com.example.traveldiary.domain.usecase.GetTravelStatsUseCase
import com.example.traveldiary.domain.usecase.ImportEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val stats: TravelStats, val passport: PassportData) : ProfileUiState
}

sealed interface ImportUiState {
    data object Idle : ImportUiState
    data object Importing : ImportUiState
    data class Success(val count: Int) : ImportUiState
    data class Error(val message: String) : ImportUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getTravelStatsUseCase: GetTravelStatsUseCase,
    getPassportUseCase: GetPassportUseCase,
    private val importEntriesUseCase: ImportEntriesUseCase,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> =
        combine(getTravelStatsUseCase(), getPassportUseCase()) { stats, passport ->
            ProfileUiState.Success(stats, passport)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading)

    private val _importState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val importState: StateFlow<ImportUiState> = _importState.asStateFlow()

    fun importDiary(sourceUri: String) {
        _importState.value = ImportUiState.Importing
        viewModelScope.launch {
            _importState.value = try {
                when (val result = importEntriesUseCase(sourceUri)) {
                    is ImportResult.Success -> ImportUiState.Success(result.entries.size)
                    ImportResult.InvalidFile -> ImportUiState.Error("No es un archivo de diario válido")
                    ImportResult.UnsupportedVersion -> ImportUiState.Error("Versión de diario no soportada")
                    ImportResult.Empty -> ImportUiState.Error("El archivo no contiene entradas")
                }
            } catch (e: Exception) {
                ImportUiState.Error(e.message ?: "Error al importar")
            }
        }
    }

    fun onImportStateConsumed() {
        _importState.value = ImportUiState.Idle
    }
}
