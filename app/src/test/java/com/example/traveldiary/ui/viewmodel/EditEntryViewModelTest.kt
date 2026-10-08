package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.GetEntryByIdUseCase
import com.example.traveldiary.domain.usecase.UpdateEntryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditEntryViewModelTest {

    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntryByIdUseCase: GetEntryByIdUseCase = mockk()
    private val updateEntryUseCase: UpdateEntryUseCase = mockk()

    private fun viewModel() = EditEntryViewModel(getEntryByIdUseCase, updateEntryUseCase)

    private fun entry(
        id: Int = 3,
        latitude: Double? = null,
        longitude: Double? = null,
    ) = TravelEntry(
        id = id,
        title = "Kyoto",
        location = "Kyoto",
        country = "Japan",
        tag = "Culture",
        imageUrl = "image.jpg",
        latitude = latitude,
        longitude = longitude,
        isFavourite = true,
    )

    private suspend fun load(scope: TestScope, viewModel: EditEntryViewModel, entry: TravelEntry?) {
        coEvery { getEntryByIdUseCase(3) } returns entry
        viewModel.loadEntry(3)
        scope.advanceUntilIdle()
    }

    @Test
    fun `load precarga todos los campos and favourite state`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)

        val state = viewModel.uiState.value as EditEntryUiState.Editing
        assertEquals(entry, state.entry)
        assertEquals(entry.toEntryDetails(), state.form)
        assertFalse(state.isDirty)
        assertTrue(state.isValid)
    }

    @Test
    fun `load reports NotFound when entry does not exist`() = runTest {
        val viewModel = viewModel()
        load(this, viewModel, null)

        assertEquals(EditEntryUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `save without complete coordinates updates same id and preserves coordinates`() = runTest {
        val entry = entry(latitude = null, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        coEvery { updateEntryUseCase(any()) } returns true

        viewModel.updateForm((viewModel.uiState.value as EditEntryUiState.Editing).form.copy(title = "Updated"))
        viewModel.onSaveClicked()
        advanceUntilIdle()

        val saved = viewModel.uiState.value as EditEntryUiState.Saved
        assertEquals(entry.id, saved.entry.id)
        assertEquals("Updated", saved.entry.title)
        assertEquals(entry.latitude, saved.entry.latitude)
        assertEquals(entry.longitude, saved.entry.longitude)
        coVerify(exactly = 1) { updateEntryUseCase(saved.entry) }
    }

    @Test
    fun `save asks for confirmation when both coordinates exist`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        viewModel.onSaveClicked()

        assertTrue(viewModel.uiState.value is EditEntryUiState.ConfirmingLocation)
        coVerify(exactly = 0) { updateEntryUseCase(any()) }
    }

    @Test
    fun `confirming current location saves original coordinates`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        coEvery { updateEntryUseCase(any()) } returns true
        viewModel.onSaveClicked()
        viewModel.keepLocationAndSave()
        advanceUntilIdle()

        val saved = viewModel.uiState.value as EditEntryUiState.Saved
        assertEquals(35.0, saved.entry.latitude)
        assertEquals(135.0, saved.entry.longitude)
    }

    @Test
    fun `choosing location update saves new coordinate pair`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        coEvery { updateEntryUseCase(any()) } returns true
        viewModel.onSaveClicked()
        viewModel.requestLocationUpdate()
        viewModel.saveWithUpdatedLocation(40.0, -3.0)
        advanceUntilIdle()

        val saved = viewModel.uiState.value as EditEntryUiState.Saved
        assertEquals(40.0, saved.entry.latitude)
        assertEquals(-3.0, saved.entry.longitude)
    }

    @Test
    fun `failed permission keeps form and does not update`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        viewModel.onSaveClicked()
        viewModel.requestLocationUpdate()
        viewModel.locationUpdateFailed()

        val state = viewModel.uiState.value as EditEntryUiState.Editing
        assertEquals(entry.toEntryDetails(), state.form)
        assertTrue(state.error!!.contains("permiso"))
        coVerify(exactly = 0) { updateEntryUseCase(any()) }
    }

    @Test
    fun `location unavailable keeps form and does not update`() = runTest {
        val entry = entry(latitude = 35.0, longitude = 135.0)
        val viewModel = viewModel()
        load(this, viewModel, entry)
        viewModel.onSaveClicked()
        viewModel.requestLocationUpdate()
        viewModel.saveWithUpdatedLocation(null, null)

        val state = viewModel.uiState.value as EditEntryUiState.Editing
        assertEquals(entry.toEntryDetails(), state.form)
        assertTrue(state.error!!.contains("GPS"))
        coVerify(exactly = 0) { updateEntryUseCase(any()) }
    }

    @Test
    fun `failed update reports NotFound`() = runTest {
        val entry = entry()
        val viewModel = viewModel()
        load(this, viewModel, entry)
        coEvery { updateEntryUseCase(any()) } returns false
        viewModel.updateForm((viewModel.uiState.value as EditEntryUiState.Editing).form.copy(title = "Updated"))
        viewModel.onSaveClicked()
        advanceUntilIdle()

        assertEquals(EditEntryUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `invalid form does not update`() = runTest {
        val entry = entry()
        val viewModel = viewModel()
        load(this, viewModel, entry)
        viewModel.updateForm((viewModel.uiState.value as EditEntryUiState.Editing).form.copy(title = " "))
        viewModel.onSaveClicked()

        val state = viewModel.uiState.value as EditEntryUiState.Editing
        assertTrue(state.error!!.contains("Completa"))
        coVerify(exactly = 0) { updateEntryUseCase(any()) }
    }

    @Test
    fun `removeImage clears image while preserving edit state`() = runTest {
        val entry = entry()
        val viewModel = viewModel()
        load(this, viewModel, entry)
        viewModel.removeImage()

        val state = viewModel.uiState.value as EditEntryUiState.Editing
        assertEquals("", state.form.imageUrl)
        assertTrue(state.isDirty)
    }
}
