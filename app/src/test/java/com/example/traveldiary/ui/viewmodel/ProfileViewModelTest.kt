package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.usecase.GetTravelStatsUseCase
import com.example.traveldiary.domain.usecase.ImportEntriesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
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
class ProfileViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getTravelStatsUseCase: GetTravelStatsUseCase = mockk()
    private val importEntriesUseCase: ImportEntriesUseCase = mockk()

    private fun viewModel(): ProfileViewModel {
        every { getTravelStatsUseCase() } returns flowOf(TravelStats(0, 0, 0, 0))
        return ProfileViewModel(getTravelStatsUseCase, importEntriesUseCase)
    }

    @Test
    fun `uiState is Loading initially`() = runTest {
        val viewModel = viewModel()
        assertEquals(ProfileUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Success with stats`() = runTest {
        val stats = TravelStats(totalTrips = 5, favourites = 2, countries = 3, continents = 2)
        every { getTravelStatsUseCase() } returns flowOf(stats)

        val viewModel = ProfileViewModel(getTravelStatsUseCase, importEntriesUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Success)
        assertEquals(stats, (state as ProfileUiState.Success).stats)
    }

    @Test
    fun `importDiary emits Success with count`() = runTest {
        val entry = TravelEntry(title = "T", location = "L", country = "C", tag = "t", imageUrl = "u")
        coEvery { importEntriesUseCase(any()) } returns ImportResult.Success(listOf(entry, entry))
        val viewModel = viewModel()

        viewModel.importDiary("content://zip")
        advanceUntilIdle()

        assertEquals(ImportUiState.Success(2), viewModel.importState.value)
    }

    @Test
    fun `importDiary maps result errors to messages`() = runTest {
        coEvery { importEntriesUseCase(any()) } returns ImportResult.UnsupportedVersion
        val viewModel = viewModel()

        viewModel.importDiary("content://zip")
        advanceUntilIdle()

        assertEquals(ImportUiState.Error("Versión de diario no soportada"), viewModel.importState.value)
    }

    @Test
    fun `importDiary emits Error on exception`() = runTest {
        coEvery { importEntriesUseCase(any()) } throws IOException("disk")
        val viewModel = viewModel()

        viewModel.importDiary("content://zip")
        advanceUntilIdle()

        assertEquals(ImportUiState.Error("disk"), viewModel.importState.value)
    }

    @Test
    fun `onImportStateConsumed resets to Idle`() = runTest {
        coEvery { importEntriesUseCase(any()) } returns ImportResult.Empty
        val viewModel = viewModel()
        viewModel.importDiary("content://zip")
        advanceUntilIdle()

        viewModel.onImportStateConsumed()

        assertEquals(ImportUiState.Idle, viewModel.importState.value)
    }
}
