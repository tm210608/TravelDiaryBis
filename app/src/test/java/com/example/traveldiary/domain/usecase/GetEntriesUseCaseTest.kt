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
import org.junit.Test

class GetEntriesUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetEntriesUseCase(repository)

    @Test
    fun `invoke returns all entries from repository`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url"),
            TravelEntry(id = 2, title = "B", location = "X", country = "Y", tag = "Z", imageUrl = "url"),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            assertEquals(entries, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getAllEntriesStream() }
    }

    @Test
    fun `invoke returns empty list when no entries`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(emptyList())

        useCase().test {
            assertEquals(emptyList<TravelEntry>(), awaitItem())
            awaitComplete()
        }
    }
}
