package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetMemoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface MemoriesUiState {
    data object Loading : MemoriesUiState
    data class Success(val entries: List<TravelEntry>) : MemoriesUiState
    data object Empty : MemoriesUiState
}

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    getMemoriesUseCase: GetMemoriesUseCase
) : ViewModel() {

    val uiState: StateFlow<MemoriesUiState> =
        getMemoriesUseCase()
            .map { entries ->
                if (entries.isEmpty()) MemoriesUiState.Empty
                else MemoriesUiState.Success(entries)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MemoriesUiState.Loading)
}
