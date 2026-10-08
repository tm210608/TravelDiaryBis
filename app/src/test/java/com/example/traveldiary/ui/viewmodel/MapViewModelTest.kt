package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetEntriesWithLocationUseCase
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
class MapViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesWithLocationUseCase: GetEntriesWithLocationUseCase = mockk()

    @Test
    fun `uiState is Loading initially`() = runTest {
        every { getEntriesWithLocationUseCase() } returns flowOf(emptyList())

        val viewModel = MapViewModel(getEntriesWithLocationUseCase)
        assertEquals(MapUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Empty when no entries have coordinates`() = runTest {
        every { getEntriesWithLocationUseCase() } returns flowOf(emptyList())

        val viewModel = MapViewModel(getEntriesWithLocationUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(MapUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Success with entries that have coordinates`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "Paris", location = "Paris", country = "France", tag = "City", imageUrl = "url", latitude = 48.85, longitude = 2.35),
            TravelEntry(id = 2, title = "Tokyo", location = "Tokyo", country = "Japan", tag = "Culture", imageUrl = "url", latitude = 35.68, longitude = 139.69),
        )
        every { getEntriesWithLocationUseCase() } returns flowOf(entries)

        val viewModel = MapViewModel(getEntriesWithLocationUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MapUiState.Success)
        assertEquals(2, (state as MapUiState.Success).entries.size)
    }

    @Test
    fun `uiState becomes Empty when entries list is empty`() = runTest {
        every { getEntriesWithLocationUseCase() } returns flowOf(emptyList())

        val viewModel = MapViewModel(getEntriesWithLocationUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(MapUiState.Empty, viewModel.uiState.value)
    }
}
