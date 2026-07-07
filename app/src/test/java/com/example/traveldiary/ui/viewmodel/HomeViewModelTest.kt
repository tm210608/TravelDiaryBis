package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val addEntryUseCase: AddEntryUseCase = mockk()

    @Test
    fun `selectedChip defaults to Todo`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        assertEquals("Todo", viewModel.selectedChip.value)
    }

    @Test
    fun `onChipSelected updates chip`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onChipSelected("Europa")
        assertEquals("Europa", viewModel.selectedChip.value)
    }

    @Test
    fun `selectedTab defaults to 0`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        assertEquals(0, viewModel.selectedTab.value)
    }

    @Test
    fun `onTabSelected updates tab`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.selectedTab.value)
    }

    @Test
    fun `searchQuery defaults to empty`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onSearchQueryChanged updates query`() {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        viewModel.onSearchQueryChanged("Paris")
        assertEquals("Paris", viewModel.searchQuery.value)
    }

    @Test
    fun `insertSampleEntry delegates to addEntryUseCase`() = runTest {
        every { getEntriesUseCase() } returns flowOf(emptyList())
        coEvery { addEntryUseCase(any()) } returns Unit

        val viewModel = HomeViewModel(getEntriesUseCase, addEntryUseCase)
        val entry = TravelEntry(title = "New", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        viewModel.insertSampleEntry(entry)

        coVerify { addEntryUseCase(entry) }
    }
}
