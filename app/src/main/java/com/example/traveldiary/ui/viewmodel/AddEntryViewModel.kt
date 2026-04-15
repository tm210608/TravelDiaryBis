package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.traveldiary.data.TravelRepository
import com.example.traveldiary.model.TravelEntry
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddEntryViewModel(private val repository: TravelRepository) : ViewModel() {

    var uiState by mutableStateOf(AddEntryUiState())
        private set

    fun updateUiState(newDetails: EntryDetails) {
        uiState = uiState.copy(entryDetails = newDetails, isEntryValid = validateInput(newDetails))
    }

    fun updateImageUrl(uri: String) {
        uiState = uiState.copy(entryDetails = uiState.entryDetails.copy(imageUrl = uri))
    }

    private fun validateInput(uiState: EntryDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && country.isNotBlank()
        }
    }

    suspend fun saveEntry(latitude: Double? = null, longitude: Double? = null) {
        if (validateInput(uiState.entryDetails)) {
            repository.insertEntry(
                uiState.entryDetails.copy(latitude = latitude, longitude = longitude).toTravelEntry()
            )
        }
    }
}

data class AddEntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false
)

data class EntryDetails(
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val country: String = "",
    val date: Instant = Instant.now(),
    val tag: String = "Trip",
    val description: String = "",
    val imageUrl: String = "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=600",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFavourite: Boolean = false
)

fun EntryDetails.toTravelEntry(): TravelEntry = TravelEntry(
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
    isFavourite = isFavourite
)

fun Instant.formatToDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}
