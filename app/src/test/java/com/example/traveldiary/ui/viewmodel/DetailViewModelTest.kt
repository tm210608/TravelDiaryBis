package com.example.traveldiary.ui.viewmodel

import app.cash.turbine.test
import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetEntryByIdUseCase
import com.example.traveldiary.domain.usecase.ToggleFavouriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntryByIdUseCase: GetEntryByIdUseCase = mockk()
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase = mockk()
    private val viewModel = DetailViewModel(getEntryByIdUseCase, toggleFavouriteUseCase)

    @Test
    fun `loadEntry with valid id returns Success state`() = runTest {
        val entry = TravelEntry(id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url")
        coEvery { getEntryByIdUseCase(1) } returns entry

        viewModel.loadEntry(1)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Success)
            assertEquals(entry, (state as DetailUiState.Success).entry)
        }
    }

    @Test
    fun `loadEntry with invalid id returns Error state`() = runTest {
        coEvery { getEntryByIdUseCase(999) } returns null

        viewModel.loadEntry(999)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Error)
        }
    }

    @Test
    fun `toggleFavourite delegates to use case`() = runTest {
        coEvery { getEntryByIdUseCase(1) } returns TravelEntry(
            id = 1, title = "A", location = "X", country = "Y", tag = "Z", imageUrl = "url",
        )
        coEvery { toggleFavouriteUseCase(1, false) } returns Unit

        viewModel.loadEntry(1)
        viewModel.toggleFavourite(1, false)

        coVerify { toggleFavouriteUseCase(1, false) }
    }
}
