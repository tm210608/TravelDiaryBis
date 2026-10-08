package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetMemoriesUseCase
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
class MemoriesViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getMemoriesUseCase: GetMemoriesUseCase = mockk()

    @Test
    fun `uiState is Loading initially`() = runTest {
        every { getMemoriesUseCase() } returns flowOf(emptyList())

        val viewModel = MemoriesViewModel(getMemoriesUseCase)
        assertEquals(MemoriesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Empty when no entries have photos`() = runTest {
        every { getMemoriesUseCase() } returns flowOf(emptyList())

        val viewModel = MemoriesViewModel(getMemoriesUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(MemoriesUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Success with entries that have photos`() = runTest {
        val entries = listOf(
            TravelEntry(id = 1, title = "Paris", location = "Paris", country = "France", tag = "City", imageUrl = "file:///a.jpg"),
        )
        every { getMemoriesUseCase() } returns flowOf(entries)

        val viewModel = MemoriesViewModel(getMemoriesUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MemoriesUiState.Success)
        assertEquals(1, (state as MemoriesUiState.Success).entries.size)
    }
}
