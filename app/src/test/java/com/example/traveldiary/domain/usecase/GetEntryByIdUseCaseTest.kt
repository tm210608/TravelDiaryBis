package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetEntryByIdUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = GetEntryByIdUseCase(repository)

    @Test
    fun `invoke returns entry by id`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        every { repository.getEntryStream(1) } returns entry

        val result = useCase(1)

        assertEquals(entry, result)
        verify(exactly = 1) { repository.getEntryStream(1) }
    }

    @Test
    fun `invoke returns null when entry not found`() = runTest {
        every { repository.getEntryStream(999) } returns null

        val result = useCase(999)

        assertNull(result)
    }
}
