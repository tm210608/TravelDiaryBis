package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
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
        private val savedStateHandle: SavedStateHandle,
        private val getProfileUseCase: GetProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val photoImporter: PhotoImporter,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
        val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                val profile = getProfileUseCase().first()
                _uiState.value =
                    if (savedStateHandle.get<Boolean>(KEY_DIRTY) == true) {
                        EditProfileUiState.Editing(
                            name = savedStateHandle[KEY_NAME] ?: profile.name,
                            photoUri = savedStateHandle[KEY_PHOTO],
                        )
                    } else {
                        EditProfileUiState.Editing(name = profile.name, photoUri = profile.photoUri)
                    }
            }
        }

        private fun updateDraft(state: EditProfileUiState.Editing) {
            savedStateHandle[KEY_DIRTY] = true
            savedStateHandle[KEY_NAME] = state.name
            if (state.photoUri != null) {
                savedStateHandle[KEY_PHOTO] = state.photoUri
            } else {
                savedStateHandle.remove<String>(KEY_PHOTO)
            }
            _uiState.value = state
        }

        fun updateName(name: String) {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            updateDraft(state.copy(name = name))
        }

        fun updatePhoto(sourceUri: String) {
            viewModelScope.launch {
                val copied = photoImporter.importPhoto(sourceUri) ?: return@launch
                val state = _uiState.value as? EditProfileUiState.Editing ?: return@launch
                updateDraft(state.copy(photoUri = copied))
            }
        }

        fun removePhoto() {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            updateDraft(state.copy(photoUri = null))
        }

        fun save() {
            val state = _uiState.value as? EditProfileUiState.Editing ?: return
            viewModelScope.launch {
                updateProfileUseCase(UserProfile(name = state.name.ifBlank { "Viajero" }, photoUri = state.photoUri))
                savedStateHandle[KEY_DIRTY] = false
                _uiState.value = EditProfileUiState.Saved
            }
        }

        companion object {
            private const val KEY_DIRTY = "draft_dirty"
            private const val KEY_NAME = "draft_name"
            private const val KEY_PHOTO = "draft_photo"
        }
    }
