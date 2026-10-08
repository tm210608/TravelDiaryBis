package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.model.continentOf
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTravelStatsUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    operator fun invoke(): Flow<TravelStats> =
        repository.getAllEntriesStream().map { entries ->
            TravelStats(
                totalTrips = entries.size,
                favourites = entries.count { it.isFavourite },
                countries = entries.map { it.country.trim() }.filter { it.isNotEmpty() }.distinct().size,
                continents = entries.mapNotNull { continentOf(it.country) }.distinct().size,
            )
        }
}
