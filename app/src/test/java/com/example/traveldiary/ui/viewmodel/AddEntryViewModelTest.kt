package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.repository.PhotoImporter
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEntryViewModelTest {
    @get:Rule
    val rule = TestDispatcherRule()

    private val addEntryUseCase: AddEntryUseCase = mockk()
    private val photoImporter: PhotoImporter = mockk()
    private val viewModel = AddEntryViewModel(addEntryUseCase, photoImporter)

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
        val details =
            EntryDetails(
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
    fun `saveEntry calls use case when valid`() =
        runTest {
            coEvery { addEntryUseCase(any()) } returns Unit

            viewModel.updateUiState(
                EntryDetails(
                    title = "Trip",
                    location = "Rome",
                    country = "Italy",
                    tag = "City",
                    imageUrl = "url",
                ),
            )
            viewModel.saveEntry()

            coVerify(atLeast = 1) { addEntryUseCase(any()) }
        }

    @Test
    fun `saveEntry does not call use case when invalid`() =
        runTest {
            val viewModel = AddEntryViewModel(addEntryUseCase, photoImporter)
            viewModel.saveEntry()

            coVerify(exactly = 0) { addEntryUseCase(any()) }
        }

    @Test
    fun `updateImageUrl updates the image URL`() {
        val uri = "content://new-image"
        viewModel.updateImageUrl(uri)
        assertEquals(uri, viewModel.uiState.entryDetails.imageUrl)
    }

    @Test
    fun `addGalleryPhotos sets first photo as cover and rest in photoUris`() =
        runTest {
            val imported = listOf("file:///a.jpg", "file:///b.jpg", "file:///c.jpg")
            coEvery { photoImporter.importPhotos(any()) } returns
                PhotoImporter.Result(imported = imported, failedCount = 0)

            viewModel.addGalleryPhotos(listOf("content://1", "content://2", "content://3"))
            advanceUntilIdle()

            assertEquals("file:///a.jpg", viewModel.uiState.entryDetails.imageUrl)
            assertEquals(listOf("file:///b.jpg", "file:///c.jpg"), viewModel.uiState.entryDetails.photoUris)
        }

    @Test
    fun `addGalleryPhotos keeps existing cover and appends rest`() =
        runTest {
            val imported = listOf("file:///a.jpg", "file:///b.jpg")
            coEvery { photoImporter.importPhotos(any()) } returns
                PhotoImporter.Result(imported = imported, failedCount = 1)

            viewModel.updateImageUrl("file:///cover.jpg")
            viewModel.addGalleryPhotos(listOf("content://1", "content://2"))
            advanceUntilIdle()

            assertEquals("file:///cover.jpg", viewModel.uiState.entryDetails.imageUrl)
            assertEquals(imported, viewModel.uiState.entryDetails.photoUris)
            assertEquals(1, viewModel.uiState.failedPhotoImports)
        }
}
