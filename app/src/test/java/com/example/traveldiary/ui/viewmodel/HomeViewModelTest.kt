package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.FilterEntriesByChipUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val filterEntriesByChipUseCase = FilterEntriesByChipUseCase()

    private fun entry(id: Int, title: String, country: String, isFavourite: Boolean = false) =
        TravelEntry(id = id, title = title, location = "L", country = country,
            tag = "X", imageUrl = "url", isFavourite = isFavourite)

    private fun viewModelWith(entries: List<TravelEntry>): HomeViewModel {
        every { getEntriesUseCase() } returns flowOf(entries)
        return HomeViewModel(getEntriesUseCase, filterEntriesByChipUseCase)
    }

    @Test
    fun `selectedChip defaults to Todo`() {
        val viewModel = viewModelWith(emptyList())
        assertEquals("Todo", viewModel.selectedChip.value)
    }

    @Test
    fun `onChipSelected updates chip`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.onChipSelected("Europa")
        assertEquals("Europa", viewModel.selectedChip.value)
    }

    @Test
    fun `entriesList filters by chip`() = runTest {
        val entries = listOf(
            entry(1, "Paris", "France"),
            entry(2, "Tokyo", "Japan"),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.entriesList.collect {} }
        advanceUntilIdle()

        viewModel.onChipSelected("Asia")
        advanceUntilIdle()

        assertEquals(listOf("Tokyo"), viewModel.entriesList.value.map { it.title })
    }

    @Test
    fun `entriesList combines chip and search query`() = runTest {
        val entries = listOf(
            entry(1, "Paris", "France"),
            entry(2, "Tokyo", "Japan"),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.entriesList.collect {} }
        advanceUntilIdle()

        viewModel.onChipSelected("Asia")
        viewModel.onSearchQueryChanged("Paris")
        advanceUntilIdle()

        assertTrue(viewModel.entriesList.value.isEmpty())
    }

    @Test
    fun `entriesList filters favourites`() = runTest {
        val entries = listOf(
            entry(1, "Paris", "France", isFavourite = true),
            entry(2, "Tokyo", "Japan"),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.entriesList.collect {} }
        advanceUntilIdle()

        viewModel.onChipSelected("Favoritos")
        advanceUntilIdle()

        assertEquals(listOf("Paris"), viewModel.entriesList.value.map { it.title })
    }

    @Test
    fun `selectedTab defaults to 0`() {
        val viewModel = viewModelWith(emptyList())
        assertEquals(0, viewModel.selectedTab.value)
    }

    @Test
    fun `onTabSelected updates tab`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.selectedTab.value)
    }

    @Test
    fun `searchQuery defaults to empty`() {
        val viewModel = viewModelWith(emptyList())
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onSearchQueryChanged updates query`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.onSearchQueryChanged("Paris")
        assertEquals("Paris", viewModel.searchQuery.value)
    }
}
