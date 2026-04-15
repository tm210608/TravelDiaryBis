package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.traveldiary.TravelDiaryApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                travelDiaryApplication().container.repository
            )
        }
        initializer {
            AddEntryViewModel(
                travelDiaryApplication().container.repository
            )
        }
    }
}

/**
 * Función de extensión para obtener la instancia de la aplicación.
 */
fun CreationExtras.travelDiaryApplication(): TravelDiaryApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TravelDiaryApplication)
