package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddEntryUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = AddEntryUseCase(repository)

    @Test
    fun `invoke inserts entry via repository`() = runTest {
        val entry = TravelEntry(title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        every { repository.insertEntry(entry) } returns Unit

        useCase(entry)

        verify(exactly = 1) { repository.insertEntry(entry) }
    }
}
