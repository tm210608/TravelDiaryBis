package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.repository.TravelRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    suspend operator fun invoke(id: Int, current: Boolean) {
        val entry = repository.getEntryStream(id) ?: return
        repository.updateEntry(entry.copy(isFavourite = !current))
    }
}
