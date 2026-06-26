package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.data.TravelRepository
import com.example.traveldiary.model.TravelEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val entry: TravelEntry) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: TravelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadEntry(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val entry = repository.getEntryStream(id)
                _uiState.value = if (entry != null) {
                    DetailUiState.Success(entry)
                } else {
                    DetailUiState.Error("Entrada no encontrada")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Error al cargar: ${e.message}")
            }
        }
    }

    fun toggleFavourite(id: Int, current: Boolean) {
        viewModelScope.launch {
            try {
                val entry = repository.getEntryStream(id) ?: return@launch
                repository.updateEntry(entry.copy(isFavourite = !current))
                loadEntry(id)
            } catch (_: Exception) { }
        }
    }
}
