package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.Continent
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.continentOf
import javax.inject.Inject

class FilterEntriesByChipUseCase @Inject constructor() {

    operator fun invoke(entries: List<TravelEntry>, chip: String): List<TravelEntry> =
        when (chip) {
            CHIP_FAVORITOS -> entries.filter { it.isFavourite }
            CHIP_TODO -> entries
            else -> {
                val continent = chipToContinent[chip] ?: return entries
                entries.filter { continentOf(it.country) == continent }
            }
        }

    companion object {
        const val CHIP_TODO = "Todo"
        const val CHIP_FAVORITOS = "Favoritos"

        private val chipToContinent = mapOf(
            "Europa" to Continent.EUROPE,
            "Asia" to Continent.ASIA,
            "América" to Continent.AMERICA,
        )
    }
}
