package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMemoriesUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    operator fun invoke(): Flow<List<TravelEntry>> =
        repository.getAllEntriesStream().map { entries ->
            entries.filter { it.imageUrl.isNotBlank() }
        }
}
