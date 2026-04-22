package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve all travel entries.
 */
class GetTravelEntriesUseCase(private val repository: TravelRepository) {
    operator fun invoke(): Flow<List<TravelEntry>> {
        return repository.getAllEntriesStream()
    }
}
