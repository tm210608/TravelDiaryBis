package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.repository.PhotoImporter
import com.example.traveldiary.domain.usecase.GetProfileUseCase
import com.example.traveldiary.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState

    data class Editing(
        val name: String,
        val photoUri: String?,
    ) : EditProfileUiState

    data object Saved : EditProfileUiState
}

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor(
        private val getProfileUseCase: GetProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val photoImporter: PhotoImporter,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
        val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                val profile = getProfileUseCase().first()
                _uiState.value = EditProfileUiState.Editing(name = profile.name, photoUri = profile.photoUri)
            }
        }

        fun updateName(name: String) {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            _uiState.value = state.copy(name = name)
        }

        fun updatePhoto(sourceUri: String) {
            viewModelScope.launch {
                val copied = photoImporter.importPhoto(sourceUri) ?: return@launch
                val state = _uiState.value as? EditProfileUiState.Editing ?: return@launch
                _uiState.value = state.copy(photoUri = copied)
            }
        }

        fun removePhoto() {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            _uiState.value = state.copy(photoUri = null)
        }

        fun save() {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            viewModelScope.launch {
                updateProfileUseCase(UserProfile(name = state.name.ifBlank { "Viajero" }, photoUri = state.photoUri))
                _uiState.value = EditProfileUiState.Saved
            }
        }
    }
