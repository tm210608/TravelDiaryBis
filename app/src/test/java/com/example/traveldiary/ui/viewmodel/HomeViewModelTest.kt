package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.ExportEntriesUseCase
import com.example.traveldiary.domain.usecase.FilterEntriesByChipUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val filterEntriesByChipUseCase = FilterEntriesByChipUseCase()
    private val exportEntriesUseCase: ExportEntriesUseCase = mockk()

    private fun entry(id: Int, title: String, country: String, isFavourite: Boolean = false) =
        TravelEntry(id = id, title = title, location = "L", country = country,
            tag = "X", imageUrl = "url", isFavourite = isFavourite)

    private fun viewModelWith(entries: List<TravelEntry>): HomeViewModel {
        every { getEntriesUseCase() } returns flowOf(entries)
        return HomeViewModel(getEntriesUseCase, filterEntriesByChipUseCase, exportEntriesUseCase)
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

    @Test
    fun `toggleSelection adds and removes id`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.toggleSelection(1)
        viewModel.toggleSelection(2)
        assertEquals(setOf(1, 2), viewModel.selectedIds.value)
        viewModel.toggleSelection(1)
        assertEquals(setOf(2), viewModel.selectedIds.value)
    }

    @Test
    fun `exitSelectionMode clears mode and selection`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.enterSelectionMode()
        viewModel.toggleSelection(1)
        viewModel.exitSelectionMode()
        assertFalse(viewModel.selectionMode.value)
        assertTrue(viewModel.selectedIds.value.isEmpty())
    }

    @Test
    fun `selectAll selects all visible entries`() = runTest {
        val viewModel = viewModelWith(listOf(entry(1, "A", "F"), entry(2, "B", "J")))
        backgroundScope.launch { viewModel.entriesList.collect {} }
        advanceUntilIdle()

        viewModel.selectAll()

        assertEquals(setOf(1, 2), viewModel.selectedIds.value)
    }

    @Test
    fun `exportSelectedTo with empty selection does nothing`() {
        val viewModel = viewModelWith(emptyList())
        viewModel.exportSelectedTo("content://d", "x.zip")
        assertEquals(ExportUiState.Idle, viewModel.exportState.value)
        coVerify(exactly = 0) { exportEntriesUseCase.saveToUri(any(), any(), any()) }
    }

    @Test
    fun `exportSelectedTo emits Success and exits selection`() = runTest {
        coEvery { exportEntriesUseCase.saveToUri(any(), any(), any()) } returns Unit
        val viewModel = viewModelWith(emptyList())
        viewModel.enterSelectionMode()
        viewModel.toggleSelection(1)

        viewModel.exportSelectedTo("content://d", "x.zip")
        advanceUntilIdle()

        assertEquals(ExportUiState.Success("x.zip"), viewModel.exportState.value)
        assertFalse(viewModel.selectionMode.value)
        coVerify { exportEntriesUseCase.saveToUri(setOf(1), "content://d", any()) }
    }

    @Test
    fun `exportSelectedTo emits Error on failure`() = runTest {
        coEvery { exportEntriesUseCase.saveToUri(any(), any(), any()) } throws IOException("boom")
        val viewModel = viewModelWith(emptyList())
        viewModel.toggleSelection(1)

        viewModel.exportSelectedTo("content://d", "x.zip")
        advanceUntilIdle()

        assertEquals(ExportUiState.Error("boom"), viewModel.exportState.value)
    }

    @Test
    fun `shareSelected emits ShareReady and exits selection`() = runTest {
        coEvery { exportEntriesUseCase.toTempZip(any(), any()) } returns File("export.zip")
        val viewModel = viewModelWith(emptyList())
        viewModel.enterSelectionMode()
        viewModel.toggleSelection(2)

        viewModel.shareSelected()
        advanceUntilIdle()

        assertEquals(ExportUiState.ShareReady("export.zip"), viewModel.exportState.value)
        assertFalse(viewModel.selectionMode.value)
        coVerify { exportEntriesUseCase.toTempZip(setOf(2), any()) }
    }

    @Test
    fun `onExportStateConsumed resets to Idle`() = runTest {
        coEvery { exportEntriesUseCase.toTempZip(any(), any()) } returns File("export.zip")
        val viewModel = viewModelWith(emptyList())
        viewModel.toggleSelection(2)
        viewModel.shareSelected()
        advanceUntilIdle()

        viewModel.onExportStateConsumed()

        assertEquals(ExportUiState.Idle, viewModel.exportState.value)
    }
}
