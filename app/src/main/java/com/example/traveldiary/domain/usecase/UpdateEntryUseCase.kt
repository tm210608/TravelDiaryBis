package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import javax.inject.Inject

class UpdateEntryUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    suspend operator fun invoke(entry: TravelEntry) = repository.updateEntry(entry)
}
