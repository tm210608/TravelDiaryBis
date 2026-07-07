package com.example.traveldiary.ui.viewmodel

import app.cash.turbine.test
import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val addEntryUseCase: AddEntryUseCase = mockk()

    private val entries = listOf(
        TravelEntry(id = 1, title = "Paris Trip", location = "Paris", country = "France", tag = "City", imageUrl = "url"),
        TravelEntry(id = 2, title = "Tokyo Adventure", location = "Tokyo", country = "Japan", tag = "Culture", imageUrl = "url"),
    )

    @Test
    fun `entriesList returns all entries when search query is blank`() = runTest {
        val flow = MutableStateFlow(entries)
        every { getEntriesUseCase() } returns flow

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)

        viewModel.entriesList.test {
            assertEquals(entries, awaitItem())
        }
    }

    @Test
    fun `entriesList filters by search query`() = runTest {
        val flow = MutableStateFlow(entries)
        every { getEntriesUseCase() } returns flow

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onSearchQueryChanged("Tokyo")

        viewModel.entriesList.test {
            assertEquals(1, awaitItem().size)
            assertEquals("Tokyo Adventure", awaitItem().first().title)
        }
    }

    @Test
    fun `entriesList filters by location in search query`() = runTest {
        val flow = MutableStateFlow(entries)
        every { getEntriesUseCase() } returns flow

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onSearchQueryChanged("Paris")

        viewModel.entriesList.test {
            assertEquals(1, awaitItem().size)
            assertEquals("Paris Trip", awaitItem().first().title)
        }
    }

    @Test
    fun `entriesList returns all when no match`() = runTest {
        val flow = MutableStateFlow(entries)
        every { getEntriesUseCase() } returns flow

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onSearchQueryChanged("NonExistent")

        viewModel.entriesList.test {
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    fun `insertSampleEntry delegates to addEntryUseCase`() = runTest {
        val flow = MutableStateFlow(emptyList<TravelEntry>())
        every { getEntriesUseCase() } returns flow
        every { addEntryUseCase(any()) } returns Unit

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        val entry = TravelEntry(title = "New", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        viewModel.insertSampleEntry(entry)

        verify { addEntryUseCase(entry) }
    }
}
