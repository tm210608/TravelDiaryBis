package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelStats
import com.example.traveldiary.domain.usecase.GetTravelStatsUseCase
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
class ProfileViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getTravelStatsUseCase: GetTravelStatsUseCase = mockk()

    @Test
    fun `uiState is Loading initially`() = runTest {
        every { getTravelStatsUseCase() } returns flowOf(TravelStats(0, 0, 0, 0))

        val viewModel = ProfileViewModel(getTravelStatsUseCase)
        assertEquals(ProfileUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Success with stats`() = runTest {
        val stats = TravelStats(totalTrips = 5, favourites = 2, countries = 3, continents = 2)
        every { getTravelStatsUseCase() } returns flowOf(stats)

        val viewModel = ProfileViewModel(getTravelStatsUseCase)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Success)
        assertEquals(stats, (state as ProfileUiState.Success).stats)
    }
}
