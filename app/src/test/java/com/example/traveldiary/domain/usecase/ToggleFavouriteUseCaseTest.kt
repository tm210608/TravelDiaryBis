package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleFavouriteUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = ToggleFavouriteUseCase(repository)

    @Test
    fun `toggle from false to true`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url", isFavourite = false)
        coEvery { repository.getEntryStream(1) } returns entry
        coEvery { repository.updateEntry(any()) } returns Unit

        useCase(1, false)

        coVerify { repository.updateEntry(any()) }
    }

    @Test
    fun `toggle from true to false`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url", isFavourite = true)
        coEvery { repository.getEntryStream(1) } returns entry
        coEvery { repository.updateEntry(any()) } returns Unit

        useCase(1, true)

        coVerify { repository.updateEntry(any()) }
    }

    @Test
    fun `toggle does nothing when entry not found`() = runTest {
        coEvery { repository.getEntryStream(999) } returns null

        useCase(999, false)

        coVerify(exactly = 0) { repository.updateEntry(any()) }
    }
}
