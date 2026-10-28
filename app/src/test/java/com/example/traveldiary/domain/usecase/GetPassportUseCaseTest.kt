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
import java.time.Instant

class GetPassportUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetPassportUseCase(repository)

    @Test
    fun `invoke computes distance legs and stamps`() = runTest {
        val entries = listOf(
            entry("España", daysAgo = 2, lat = 40.4168, lon = -3.7038),
            entry("España", daysAgo = 1, lat = 41.3851, lon = 2.1734),
            entry("Japan", daysAgo = 1, lat = null, lon = null),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val passport = awaitItem()
            assertEquals(505.0, passport.kmTotal, 12.0)
            assertEquals(1, passport.legs)
            assertEquals(2, passport.stamps.size)
            assertEquals("ESP", passport.stamps.first { it.code == "ESP" }.let { it.code })
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty passport when no entries`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(emptyList())

        useCase().test {
            val passport = awaitItem()
            assertEquals(0.0, passport.kmTotal, 0.001)
            assertEquals(0, passport.legs)
            assertEquals(0, passport.stamps.size)
            awaitComplete()
        }
    }

    private fun entry(country: String, daysAgo: Int, lat: Double?, lon: Double?) = TravelEntry(
        id = 0,
        title = "T",
        location = "L",
        country = country,
        date = Instant.now().minusSeconds(daysAgo * 86400L),
        tag = "X",
        imageUrl = "url",
        latitude = lat,
        longitude = lon,
    )
}
