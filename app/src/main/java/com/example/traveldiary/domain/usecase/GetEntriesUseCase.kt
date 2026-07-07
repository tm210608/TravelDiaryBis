package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEntriesUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    operator fun invoke(): Flow<List<TravelEntry>> = repository.getAllEntriesStream()
}
