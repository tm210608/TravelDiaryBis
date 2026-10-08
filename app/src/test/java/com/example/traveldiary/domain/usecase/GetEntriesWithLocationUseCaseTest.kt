package com.example.traveldiary.domain.usecase

import app.cash.turbine.test
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetEntriesWithLocationUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetEntriesWithLocationUseCase(repository)

    @Test
    fun `invoke returns only entries with non-null latitude and longitude`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "Paris", location = "X", country = "Y", tag = "Z", imageUrl = "url", latitude = 48.85, longitude = 2.35),
            TravelEntry(id = 2, title = "NoCoords", location = "X", country = "Y", tag = "Z", imageUrl = "url"),
            TravelEntry(id = 3, title = "Tokyo", location = "X", country = "Y", tag = "Z", imageUrl = "url", latitude = 35.68, longitude = 139.69),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Paris", result[0].title)
            assertEquals("Tokyo", result[1].title)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getAllEntriesStream() }
    }

    @Test
    fun `invoke returns empty list when all entries lack coordinates`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url"),
            TravelEntry(id = 2, title = "B", location = "X", country = "Y", tag = "Z", imageUrl = "url"),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when repository is empty`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(emptyList())

        useCase().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke filters out entries with only latitude or only longitude`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "OnlyLat", location = "X", country = "Y", tag = "Z", imageUrl = "url", latitude = 48.85, longitude = null),
            TravelEntry(id = 2, title = "OnlyLng", location = "X", country = "Y", tag = "Z", imageUrl = "url", latitude = null, longitude = 2.35),
            TravelEntry(id = 3, title = "Both", location = "X", country = "Y", tag = "Z", imageUrl = "url", latitude = 35.68, longitude = 139.69),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Both", result[0].title)
            awaitComplete()
        }
    }
}
