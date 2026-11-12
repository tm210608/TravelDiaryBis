package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.PhotoImporter
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel
    @Inject
    constructor(
        private val addEntryUseCase: AddEntryUseCase,
        private val photoImporter: PhotoImporter,
    ) : ViewModel() {
        var uiState by mutableStateOf(AddEntryUiState())
            private set

        fun updateUiState(newDetails: EntryDetails) {
            uiState = uiState.copy(entryDetails = newDetails, isEntryValid = validateInput(newDetails))
        }

        fun updateImageUrl(uri: String) {
            uiState = uiState.copy(entryDetails = uiState.entryDetails.copy(imageUrl = uri))
        }

        fun addGalleryPhotos(uris: List<String>) {
            if (uris.isEmpty()) return
            viewModelScope.launch {
                uiState = uiState.copy(importingPhotos = true)
                val result = photoImporter.importPhotos(uris)
                val current = uiState.entryDetails
                val newDetails =
                    if (current.imageUrl.isBlank() && result.imported.isNotEmpty()) {
                        current.copy(
                            imageUrl = result.imported.first(),
                            photoUris = current.photoUris + result.imported.drop(1),
                        )
                    } else {
                        current.copy(photoUris = current.photoUris + result.imported)
                    }
                uiState =
                    uiState.copy(
                        entryDetails = newDetails,
                        importingPhotos = false,
                        failedPhotoImports = result.failedCount,
                    )
            }
        }

        fun onPhotoImportMessageConsumed() {
            uiState = uiState.copy(failedPhotoImports = 0)
        }

        fun removeGalleryPhoto(uri: String) {
            uiState =
                uiState.copy(
                    entryDetails =
                        uiState.entryDetails.copy(
                            photoUris = uiState.entryDetails.photoUris - uri,
                        ),
                )
        }

        private fun validateInput(uiState: EntryDetails): Boolean {
            return with(uiState) {
                title.isNotBlank() && location.isNotBlank() && country.isNotBlank()
            }
        }

        suspend fun saveEntry(
            latitude: Double? = null,
            longitude: Double? = null,
        ) {
            if (validateInput(uiState.entryDetails)) {
                try {
                    addEntryUseCase(
                        uiState.entryDetails.copy(latitude = latitude, longitude = longitude).toTravelEntry(),
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

data class AddEntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false,
    val importingPhotos: Boolean = false,
    val failedPhotoImports: Int = 0,
)

data class EntryDetails(
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val country: String = "",
    val date: Instant = Instant.now(),
    val tag: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val photoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFavourite: Boolean = false,
    val uuid: String = UUID.randomUUID().toString(),
)

fun EntryDetails.toTravelEntry(): TravelEntry =
    TravelEntry(
        id = id,
        title = title,
        location = location,
        country = country,
        date = date,
        tag = tag.ifBlank { "Viaje" },
        description = description,
        imageUrl = imageUrl,
        photoUris = photoUris,
        latitude = latitude,
        longitude = longitude,
        isFavourite = isFavourite,
        uuid = uuid,
    )
