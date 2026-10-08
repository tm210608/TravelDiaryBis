package com.example.traveldiary.domain.usecase

import app.cash.turbine.test
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMemoriesUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetMemoriesUseCase(repository)

    @Test
    fun `invoke returns only entries with non-blank imageUrl`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "ConFoto", location = "X", country = "Y", tag = "Z", imageUrl = "file:///a.jpg"),
            TravelEntry(id = 2, title = "SinFoto", location = "X", country = "Y", tag = "Z", imageUrl = ""),
            TravelEntry(id = 3, title = "Blanco", location = "X", country = "Y", tag = "Z", imageUrl = "   "),
        )
        every { repository.getAllEntriesStream() } returns flowOf(entries)

        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("ConFoto", result[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty when no entry has a photo`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(
            listOf(TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = ""))
        )

        useCase().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }
}
