package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.HomeFilter
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import com.example.traveldiary.domain.usecase.FilterEntriesUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val addEntryUseCase: AddEntryUseCase = mockk(relaxed = true)

    private val paris = TravelEntry(id = 1, title = "Paris", location = "Paris", country = "Francia", tag = "T", imageUrl = "url")
    private val kyoto = TravelEntry(id = 2, title = "Kyoto", location = "Kyoto", country = "Japón", tag = "T", imageUrl = "url", isFavourite = true)
    private val rome = TravelEntry(id = 3, title = "Roma", location = "Roma", country = "Italia", tag = "T", imageUrl = "url", isFavourite = true)

    private fun createViewModel(
        entries: List<TravelEntry> = emptyList(),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): HomeViewModel {
        every { getEntriesUseCase() } returns flowOf(entries)
        return HomeViewModel(getEntriesUseCase, addEntryUseCase, FilterEntriesUseCase(), savedStateHandle)
    }

    private fun TestScope.collect(viewModel: HomeViewModel) {
        backgroundScope.launch { viewModel.listState.collect {} }
        advanceUntilIdle()
    }

    @Test
    fun `selectedFilter defaults to TODO`() {
        assertEquals(HomeFilter.TODO, createViewModel().selectedFilter.value)
    }

    @Test
    fun `onFilterSelected updates filter`() {
        val viewModel = createViewModel()
        viewModel.onFilterSelected(HomeFilter.EUROPA)
        assertEquals(HomeFilter.EUROPA, viewModel.selectedFilter.value)
    }

    @Test
    fun `listState shows all entries and favourites for TODO`() = runTest {
        val viewModel = createViewModel(listOf(paris, kyoto, rome))
        collect(viewModel)

        assertEquals(
            HomeListState.Results(listOf(paris, kyoto, rome), listOf(kyoto, rome)),
            viewModel.listState.value,
        )
    }

    @Test
    fun `listState filters entries and featured by selected chip`() = runTest {
        val viewModel = createViewModel(listOf(paris, kyoto, rome))
        collect(viewModel)

        viewModel.onFilterSelected(HomeFilter.EUROPA)
        advanceUntilIdle()

        assertEquals(HomeListState.Results(listOf(paris, rome), listOf(rome)), viewModel.listState.value)
    }

    @Test
    fun `listState combines chip and search query`() = runTest {
        val viewModel = createViewModel(listOf(paris, kyoto, rome))
        collect(viewModel)

        viewModel.onFilterSelected(HomeFilter.EUROPA)
        viewModel.onSearchQueryChanged("roma")
        advanceUntilIdle()

        assertEquals(HomeListState.Results(listOf(rome), listOf(rome)), viewModel.listState.value)
    }

    @Test
    fun `listState is NoResults when filter leaves nothing but database has entries`() = runTest {
        val viewModel = createViewModel(listOf(paris, rome))
        collect(viewModel)

        viewModel.onFilterSelected(HomeFilter.ASIA)
        advanceUntilIdle()

        assertEquals(HomeListState.NoResults(HomeFilter.ASIA, ""), viewModel.listState.value)
    }

    @Test
    fun `listState is empty Results when database is empty`() = runTest {
        val viewModel = createViewModel(emptyList())
        collect(viewModel)

        viewModel.onFilterSelected(HomeFilter.ASIA)
        advanceUntilIdle()

        assertEquals(HomeListState.Results(emptyList(), emptyList()), viewModel.listState.value)
    }

    @Test
    fun `onShowAll resets chip to TODO and clears search`() = runTest {
        val viewModel = createViewModel(listOf(paris, kyoto, rome))
        collect(viewModel)
        viewModel.onFilterSelected(HomeFilter.ASIA)
        viewModel.onSearchQueryChanged("roma")
        advanceUntilIdle()

        viewModel.onShowAll()
        advanceUntilIdle()

        assertEquals(HomeFilter.TODO, viewModel.selectedFilter.value)
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(HomeListState.Results(listOf(paris, kyoto, rome), listOf(kyoto, rome)), viewModel.listState.value)
    }

    @Test
    fun `selected chip is restored from SavedStateHandle after recreation`() = runTest {
        val handle = SavedStateHandle()
        createViewModel(listOf(paris, kyoto, rome), handle).onFilterSelected(HomeFilter.ASIA)

        val recreated = createViewModel(listOf(paris, kyoto, rome), handle)
        collect(recreated)

        assertEquals(HomeFilter.ASIA, recreated.selectedFilter.value)
        assertEquals(HomeListState.Results(listOf(kyoto), listOf(kyoto)), recreated.listState.value)
    }

    @Test
    fun `selectedTab defaults to 0`() {
        assertEquals(0, createViewModel().selectedTab.value)
    }

    @Test
    fun `onTabSelected updates tab`() {
        val viewModel = createViewModel()
        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.selectedTab.value)
    }

    @Test
    fun `searchQuery defaults to empty`() {
        assertEquals("", createViewModel().searchQuery.value)
    }

    @Test
    fun `onSearchQueryChanged updates query`() {
        val viewModel = createViewModel()
        viewModel.onSearchQueryChanged("Paris")
        assertEquals("Paris", viewModel.searchQuery.value)
    }

    @Test
    fun `insertSampleEntry delegates to addEntryUseCase`() = runTest {
        val viewModel = createViewModel()
        val entry = TravelEntry(title = "New", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        viewModel.insertSampleEntry(entry)

        coVerify { addEntryUseCase(entry) }
    }
}
