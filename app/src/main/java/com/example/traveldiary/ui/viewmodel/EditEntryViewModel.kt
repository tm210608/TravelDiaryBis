package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetEntryByIdUseCase
import com.example.traveldiary.domain.usecase.UpdateEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditEntryUiState {
    data object Loading : EditEntryUiState
    data class Editing(
        val entry: TravelEntry,
        val form: EntryDetails,
        val error: String? = null,
    ) : EditEntryUiState {
        val isDirty: Boolean get() = form.toTravelEntry() != entry
        val isValid: Boolean get() = with(form) {
            title.isNotBlank() && location.isNotBlank() && country.isNotBlank()
        }
    }
    data class ConfirmingLocation(val entry: TravelEntry, val form: EntryDetails) : EditEntryUiState
    data class UpdatingLocation(val entry: TravelEntry, val form: EntryDetails) : EditEntryUiState
    data class Saving(val form: EntryDetails) : EditEntryUiState
    data class Saved(val entry: TravelEntry) : EditEntryUiState
    data class Error(val message: String) : EditEntryUiState
    data object NotFound : EditEntryUiState
}

@HiltViewModel
class EditEntryViewModel @Inject constructor(
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditEntryUiState>(EditEntryUiState.Loading)
    val uiState: StateFlow<EditEntryUiState> = _uiState.asStateFlow()
    private var pendingImageUrl: String? = null
    private var loadedEntryId: Int? = null
    private var loadingEntryId: Int? = null

    fun loadEntry(id: Int) {
        if (loadedEntryId == id || loadingEntryId == id) return
        loadingEntryId = id
        viewModelScope.launch {
            _uiState.value = EditEntryUiState.Loading
            try {
                val entry = getEntryByIdUseCase(id)
                loadedEntryId = id
                _uiState.value = if (entry == null) {
                    EditEntryUiState.NotFound
                } else {
                    val form = entry.toEntryDetails().let { currentForm ->
                        pendingImageUrl?.let { currentForm.copy(imageUrl = it) } ?: currentForm
                    }
                    pendingImageUrl = null
                    EditEntryUiState.Editing(entry, form)
                }
            } catch (exception: Exception) {
                _uiState.value = EditEntryUiState.Error(exception.message ?: "No se pudo cargar la entrada")
            } finally {
                if (loadingEntryId == id) loadingEntryId = null
            }
        }
    }

    fun updateForm(form: EntryDetails) {
        val state = _uiState.value as? EditEntryUiState.Editing ?: return
        _uiState.value = state.copy(form = form, error = null)
    }

    fun updateImageUrl(uri: String) {
        val state = _uiState.value as? EditEntryUiState.Editing
        if (state == null) {
            pendingImageUrl = uri
            return
        }
        updateForm(state.form.copy(imageUrl = uri))
    }

    fun removeImage() {
        updateImageUrl("")
    }

    fun onSaveClicked() {
        val state = _uiState.value as? EditEntryUiState.Editing ?: return
        if (!state.isValid) {
            _uiState.value = state.copy(error = "Completa el título, la ubicación y el país")
            return
        }
        if (state.entry.latitude != null && state.entry.longitude != null) {
            _uiState.value = EditEntryUiState.ConfirmingLocation(state.entry, state.form)
        } else {
            save(state.entry, state.form, state.entry.latitude, state.entry.longitude)
        }
    }

    fun keepLocationAndSave() {
        val state = _uiState.value as? EditEntryUiState.ConfirmingLocation ?: return
        save(state.entry, state.form, state.entry.latitude, state.entry.longitude)
    }

    fun requestLocationUpdate() {
        val state = _uiState.value as? EditEntryUiState.ConfirmingLocation ?: return
        _uiState.value = EditEntryUiState.UpdatingLocation(state.entry, state.form)
    }

    fun saveWithUpdatedLocation(latitude: Double?, longitude: Double?) {
        val state = _uiState.value as? EditEntryUiState.UpdatingLocation ?: return
        if (latitude == null || longitude == null) {
            _uiState.value = EditEntryUiState.Editing(
                state.entry,
                state.form,
                "No se pudo obtener la ubicación. Revisa el GPS e inténtalo de nuevo.",
            )
            return
        }
        save(state.entry, state.form, latitude, longitude)
    }

    fun locationUpdateFailed() {
        val state = _uiState.value as? EditEntryUiState.UpdatingLocation ?: return
        _uiState.value = EditEntryUiState.Editing(
            state.entry,
            state.form,
            "No se concedió el permiso de ubicación. Puedes volver a intentarlo.",
        )
    }

    fun cancelLocationConfirmation() {
        val state = _uiState.value as? EditEntryUiState.ConfirmingLocation ?: return
        _uiState.value = EditEntryUiState.Editing(state.entry, state.form)
    }

    private fun save(entry: TravelEntry, form: EntryDetails, latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            _uiState.value = EditEntryUiState.Saving(form)
            val updatedEntry = form.toTravelEntry().copy(
                id = entry.id,
                latitude = latitude,
                longitude = longitude,
            )
            try {
                if (updateEntryUseCase(updatedEntry)) {
                    _uiState.value = EditEntryUiState.Saved(updatedEntry)
                } else {
                    _uiState.value = EditEntryUiState.NotFound
                }
            } catch (exception: Exception) {
                _uiState.value = EditEntryUiState.Editing(
                    entry,
                    form,
                    exception.message ?: "No se pudieron guardar los cambios",
                )
            }
        }
    }
}

fun TravelEntry.toEntryDetails() = EntryDetails(
    id = id,
    title = title,
    location = location,
    country = country,
    date = date,
    tag = tag,
    description = description,
    imageUrl = imageUrl,
    latitude = latitude,
    longitude = longitude,
    isFavourite = isFavourite,
    uuid = uuid,
)
