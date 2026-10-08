package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.usecase.GetTravelStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val stats: TravelStats) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getTravelStatsUseCase: GetTravelStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> =
        getTravelStatsUseCase()
            .map { ProfileUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading)
}
