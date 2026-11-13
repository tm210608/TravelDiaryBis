package com.example.traveldiary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.repository.PhotoImporter
import com.example.traveldiary.domain.usecase.GetProfileUseCase
import com.example.traveldiary.domain.usecase.UpdateProfileUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {
    @get:Rule
    val rule = TestDispatcherRule()

    private val getProfileUseCase: GetProfileUseCase = mockk()
    private val updateProfileUseCase: UpdateProfileUseCase = mockk()
    private val photoImporter: PhotoImporter = mockk()

    private fun viewModel(
        profile: UserProfile = UserProfile(name = "Ana"),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): EditProfileViewModel {
        every { getProfileUseCase() } returns flowOf(profile)
        return EditProfileViewModel(savedStateHandle, getProfileUseCase, updateProfileUseCase, photoImporter)
    }

    @Test
    fun `loads profile into Editing state`() =
        runTest {
            val viewModel = viewModel(UserProfile(name = "Ana", photoUri = "file:///p.jpg"))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is EditProfileUiState.Editing)
            assertEquals("Ana", (state as EditProfileUiState.Editing).name)
            assertEquals("file:///p.jpg", state.photoUri)
        }

    @Test
    fun `updatePhoto imports and sets photo uri`() =
        runTest {
            coEvery { photoImporter.importPhoto("content://x") } returns "file:///avatar.jpg"
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.updatePhoto("content://x")
            advanceUntilIdle()

            val state = viewModel.uiState.value as EditProfileUiState.Editing
            assertEquals("file:///avatar.jpg", state.photoUri)
        }

    @Test
    fun `save persists profile and moves to Saved`() =
        runTest {
            coEvery { updateProfileUseCase(any()) } returns Unit
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.updateName("Juan")
            viewModel.save()
            advanceUntilIdle()

            coVerify { updateProfileUseCase(UserProfile(name = "Juan", photoUri = null)) }
            assertEquals(EditProfileUiState.Saved, viewModel.uiState.value)
        }

    @Test
    fun `blank name saves Viajero default`() =
        runTest {
            coEvery { updateProfileUseCase(any()) } returns Unit
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.updateName("   ")
            viewModel.save()
            advanceUntilIdle()

            coVerify { updateProfileUseCase(UserProfile(name = "Viajero", photoUri = null)) }
        }

    @Test
    fun `restores draft from saved state after recreation`() =
        runTest {
            val handle = SavedStateHandle()
            val first = viewModel(savedStateHandle = handle)
            advanceUntilIdle()

            first.updateName("Lucia")

            val second = viewModel(savedStateHandle = handle)
            advanceUntilIdle()

            val state = second.uiState.value as EditProfileUiState.Editing
            assertEquals("Lucia", state.name)
        }
}
