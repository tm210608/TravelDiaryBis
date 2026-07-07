package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddEntryViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val addEntryUseCase: AddEntryUseCase = mockk()
    private val viewModel = AddEntryViewModel(addEntryUseCase)

    @Test
    fun `initial state has empty details and invalid`() {
        val state = viewModel.uiState
        assertEquals("", state.entryDetails.title)
        assertEquals("", state.entryDetails.location)
        assertEquals("", state.entryDetails.country)
        assertFalse(state.isEntryValid)
    }

    @Test
    fun `updateUiState changes entry details`() {
        val details = EntryDetails(
            title = "My Trip",
            location = "Paris",
            country = "France",
            tag = "City",
            imageUrl = "url",
        )
        viewModel.updateUiState(details)
        assertEquals("My Trip", viewModel.uiState.entryDetails.title)
        assertTrue(viewModel.uiState.isEntryValid)
    }

    @Test
    fun `saveEntry calls use case when valid`() = runTest {
        coEvery { addEntryUseCase(any()) } returns Unit

        viewModel.updateUiState(EntryDetails(
            title = "Trip",
            location = "Rome",
            country = "Italy",
            tag = "City",
            imageUrl = "url",
        ))
        viewModel.saveEntry()

        coVerify(atLeast = 1) { addEntryUseCase(any()) }
    }

    @Test
    fun `saveEntry does not call use case when invalid`() = runTest {
        val viewModel = AddEntryViewModel(addEntryUseCase)
        viewModel.saveEntry()

        coVerify(exactly = 0) { addEntryUseCase(any()) }
    }

    @Test
    fun `updateImageUrl updates the image URL`() {
        val uri = "content://new-image"
        viewModel.updateImageUrl(uri)
        assertEquals(uri, viewModel.uiState.entryDetails.imageUrl)
    }
}
