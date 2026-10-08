package com.example.traveldiary.domain.usecase

import app.cash.turbine.test
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTravelStatsUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetTravelStatsUseCase(repository)

    private fun entry(id: Int, country: String, isFavourite: Boolean = false) =
        TravelEntry(id = id, title = "T$id", location = "L", country = country,
            tag = "X", imageUrl = "url", isFavourite = isFavourite)

    @Test
    fun `invoke computes trips favourites countries and continents`() = runTest {
        val entries = listOf(
            entry(1, "France", isFavourite = true),
            entry(2, "France"),
            entry(3, "Japan", isFavourite = true),
            entry(4, "Brazil"),
            entry(5, "Atlantis"),
            entry(6, "  "),                  // país vacío: ni país ni continente
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val stats = awaitItem()
            assertEquals(6, stats.totalTrips)
            assertEquals(2, stats.favourites)
            assertEquals(4, stats.countries)   // France, Japan, Brazil, Atlantis
            assertEquals(3, stats.continents)  // EUROPE, ASIA, AMERICA
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns zeros when there are no entries`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(emptyList())

        useCase().test {
            val stats = awaitItem()
            assertEquals(0, stats.totalTrips)
            assertEquals(0, stats.favourites)
            assertEquals(0, stats.countries)
            assertEquals(0, stats.continents)
            awaitComplete()
        }
    }
}
