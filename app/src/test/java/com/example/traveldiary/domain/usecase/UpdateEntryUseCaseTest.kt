package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateEntryUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = UpdateEntryUseCase(repository)

    @Test
    fun `invoke updates entry via repository`() = runTest {
        val entry = TravelEntry(
            id = 1,
            title = "Updated Title",
            location = "Madrid",
            country = "Spain",
            tag = "City",
            imageUrl = "url"
        )
        coEvery { repository.updateEntry(entry) } returns true

        val updated = useCase(entry)

        assertTrue(updated)
        coVerify(exactly = 1) { repository.updateEntry(entry) }
    }

    @Test
    fun `invoke returns false when repository does not update a row`() = runTest {
        val entry = TravelEntry(
            id = 99,
            title = "Missing",
            location = "Nowhere",
            country = "Unknown",
            tag = "Trip",
            imageUrl = ""
        )
        coEvery { repository.updateEntry(entry) } returns false

        assertFalse(useCase(entry))
    }
}
