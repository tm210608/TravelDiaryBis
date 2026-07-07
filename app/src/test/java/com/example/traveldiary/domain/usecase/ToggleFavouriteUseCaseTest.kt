package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ToggleFavouriteUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = ToggleFavouriteUseCase(repository)

    @Test
    fun `toggle from false to true`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url", isFavourite = false)
        every { repository.getEntryStream(1) } returns entry
        every { repository.updateEntry(any()) } returns Unit

        useCase(1, false)

        verify { repository.updateEntry(match { it.isFavourite }) }
    }

    @Test
    fun `toggle from true to false`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url", isFavourite = true)
        every { repository.getEntryStream(1) } returns entry
        every { repository.updateEntry(any()) } returns Unit

        useCase(1, true)

        verify { repository.updateEntry(match { !it.isFavourite }) }
    }

    @Test
    fun `toggle does nothing when entry not found`() = runTest {
        every { repository.getEntryStream(999) } returns null

        useCase(999, false)

        verify(exactly = 0) { repository.updateEntry(any()) }
    }
}
