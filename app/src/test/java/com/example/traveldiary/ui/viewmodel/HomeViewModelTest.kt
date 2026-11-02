package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.WeatherReading
import com.example.traveldiary.domain.repository.WeatherRepository
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
    private val weatherRepository: WeatherRepository = mockk()

    private fun entry(
        id: Int,
        title: String,
        country: String,
        isFavourite: Boolean = false,
        lat: Double? = null,
        lon: Double? = null,
    ) = TravelEntry(id = id, title = title, location = "L", country = country,
        tag = "X", imageUrl = "url", isFavourite = isFavourite,
        latitude = lat, longitude = lon)

    private fun viewModelWith(entries: List<TravelEntry>): HomeViewModel {
        every { getEntriesUseCase() } returns flowOf(entries)
        return HomeViewModel(
            getEntriesUseCase, filterEntriesByChipUseCase, exportEntriesUseCase, weatherRepository
        )
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
    fun `chipCounts counts each chip respecting search`() = runTest {
        val entries = listOf(
            entry(1, "Paris", "France"),
            entry(2, "Tokyo", "Japan"),
            entry(3, "Kyoto", "Japan", isFavourite = true),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.chipCounts.collect {} }
        advanceUntilIdle()

        assertEquals(3, viewModel.chipCounts.value["Todo"])
        assertEquals(1, viewModel.chipCounts.value["Europa"])
        assertEquals(2, viewModel.chipCounts.value["Asia"])
        assertEquals(0, viewModel.chipCounts.value["América"])
        assertEquals(1, viewModel.chipCounts.value["Favoritos"])

        viewModel.onSearchQueryChanged("Tokyo")
        advanceUntilIdle()

        assertEquals(1, viewModel.chipCounts.value["Todo"])
        assertEquals(0, viewModel.chipCounts.value["Europa"])
        assertEquals(1, viewModel.chipCounts.value["Asia"])
        assertEquals(0, viewModel.chipCounts.value["Favoritos"])
    }

    @Test
    fun `featuredEntries prefers favourites with fallback to recent`() = runTest {
        val entries = listOf(
            entry(1, "Paris", "France", isFavourite = true),
            entry(2, "Tokyo", "Japan"),
            entry(3, "Rome", "Italy"),
            entry(4, "Berlin", "Germany"),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.featuredEntries.collect {} }
        advanceUntilIdle()

        assertEquals(listOf("Paris"), viewModel.featuredEntries.value.map { it.title })

        val viewModelNoFav = viewModelWith(entries.map { it.copy(isFavourite = false) })
        backgroundScope.launch { viewModelNoFav.featuredEntries.collect {} }
        advanceUntilIdle()

        assertEquals(3, viewModelNoFav.featuredEntries.value.size)
    }

    @Test
    fun `featuredWeather fetches only GPS featured entries`() = runTest {
        coEvery { weatherRepository.weatherAt(any(), any(), any()) } returns
            WeatherReading(16.4, 45)
        val entries = listOf(
            entry(1, "Con GPS", "Spain", isFavourite = true, lat = 40.4, lon = -3.7),
            entry(2, "Sin GPS", "Japan", isFavourite = true),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.featuredWeather.collect {} }
        advanceUntilIdle()

        assertEquals(mapOf(1 to WeatherReading(16.4, 45)), viewModel.featuredWeather.value)
        coVerify(exactly = 1) { weatherRepository.weatherAt(40.4, -3.7, any()) }
    }

    @Test
    fun `featuredWeather stays empty when repository throws`() = runTest {
        coEvery { weatherRepository.weatherAt(any(), any(), any()) } throws
            java.io.IOException("offline")
        val entries = listOf(
            entry(1, "Con GPS", "Spain", lat = 40.4, lon = -3.7),
        )
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.featuredWeather.collect {} }
        advanceUntilIdle()

        assertTrue(viewModel.featuredWeather.value.isEmpty())
    }

    @Test
    fun `featuredWeather does not fetch for entries without GPS`() = runTest {
        val entries = listOf(entry(1, "Sin GPS", "Japan"))
        val viewModel = viewModelWith(entries)
        backgroundScope.launch { viewModel.featuredWeather.collect {} }
        advanceUntilIdle()

        coVerify(exactly = 0) { weatherRepository.weatherAt(any(), any(), any()) }
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
