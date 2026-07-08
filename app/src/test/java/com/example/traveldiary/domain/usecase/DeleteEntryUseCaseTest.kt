package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteEntryUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = DeleteEntryUseCase(repository)

    @Test
    fun `invoke deletes entry via repository`() = runTest {
        val entry = TravelEntry(
            id = 1,
            title = "Paris Trip",
            location = "Paris",
            country = "France",
            tag = "City",
            imageUrl = "url"
        )
        coEvery { repository.deleteEntry(entry) } returns Unit

        useCase(entry)

        coVerify(exactly = 1) { repository.deleteEntry(entry) }
    }
}
