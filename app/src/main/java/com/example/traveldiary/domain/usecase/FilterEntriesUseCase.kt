package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.ContinentResolver
import com.example.traveldiary.domain.model.Continent
import com.example.traveldiary.domain.model.HomeFilter
import com.example.traveldiary.domain.model.TravelEntry
import javax.inject.Inject

class FilterEntriesUseCase @Inject constructor() {

    operator fun invoke(
        entries: List<TravelEntry>,
        filter: HomeFilter,
        query: String,
    ): List<TravelEntry> = entries.filter { it.matches(filter) && it.matches(query) }

    private fun TravelEntry.matches(filter: HomeFilter): Boolean = when (filter) {
        HomeFilter.TODO -> true
        HomeFilter.FAVORITOS -> isFavourite
        HomeFilter.EUROPA -> ContinentResolver.resolve(country) == Continent.EUROPA
        HomeFilter.ASIA -> ContinentResolver.resolve(country) == Continent.ASIA
        HomeFilter.AMERICA -> ContinentResolver.resolve(country) == Continent.AMERICA
    }

    private fun TravelEntry.matches(query: String): Boolean =
        query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            location.contains(query, ignoreCase = true) ||
            country.contains(query, ignoreCase = true)
}
