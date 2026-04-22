package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository

/**
 * Use case to add a new travel entry.
 */
class AddTravelEntryUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(entry: TravelEntry) {
        repository.insertEntry(entry)
    }
}
