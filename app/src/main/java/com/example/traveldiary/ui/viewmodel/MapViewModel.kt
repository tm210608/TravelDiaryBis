package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetEntriesWithLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface MapUiState {
    data object Loading : MapUiState
    data class Success(val entries: List<TravelEntry>) : MapUiState
    data object Empty : MapUiState
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getEntriesWithLocationUseCase: GetEntriesWithLocationUseCase
) : ViewModel() {

    val uiState: StateFlow<MapUiState> =
        getEntriesWithLocationUseCase()
            .map { entries ->
                if (entries.isEmpty()) {
                    MapUiState.Empty
                } else {
                    MapUiState.Success(entries)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapUiState.Loading)
}
