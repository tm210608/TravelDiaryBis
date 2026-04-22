package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.traveldiary.domain.repository.TravelRepository
import com.example.traveldiary.domain.model.TravelEntry
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Representa los estados de la pantalla de añadir entrada.
 */
data class AddEntryUiState(
    val detalleEntrada: EntryDetails = EntryDetails(),
    val esValido: Boolean = false,
    val estaGuardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val error: String? = null
)

class AddEntryViewModel(private val repository: TravelRepository) : ViewModel() {

    var uiState by mutableStateOf(AddEntryUiState())
        private set

    fun updateUiState(newDetails: EntryDetails) {
        uiState = uiState.copy(
            detalleEntrada = newDetails, 
            esValido = validateInput(newDetails)
        )
    }

    fun updateImageUrl(uri: String) {
        uiState = uiState.copy(
            detalleEntrada = uiState.detalleEntrada.copy(imageUrl = uri)
        )
    }

    private fun validateInput(uiState: EntryDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && country.isNotBlank()
        }
    }

    fun saveEntry(latitude: Double? = null, longitude: Double? = null) {
        if (validateInput(uiState.detalleEntrada)) {
            uiState = uiState.copy(estaGuardando = true)
            viewModelScope.launch {
                try {
                    repository.insertEntry(
                        uiState.detalleEntrada.copy(
                            latitude = latitude, 
                            longitude = longitude
                        ).toTravelEntry()
                    )
                    uiState = uiState.copy(estaGuardando = false, guardadoExitoso = true)
                } catch (e: Exception) {
                    uiState = uiState.copy(estaGuardando = false, error = e.message)
                }
            }
        }
    }
}

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
