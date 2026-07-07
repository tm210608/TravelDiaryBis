package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddEntryUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val useCase = AddEntryUseCase(repository)

    @Test
    fun `invoke inserts entry via repository`() = runTest {
        val entry = TravelEntry(title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        coEvery { repository.insertEntry(entry) } returns Unit

        useCase(entry)

        coVerify(exactly = 1) { repository.insertEntry(entry) }
    }
}
