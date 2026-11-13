package com.example.traveldiary.ui.viewmodel

import com.example.traveldiary.TestDispatcherRule
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.usecase.ExportEntriesUseCase
import com.example.traveldiary.domain.usecase.ExportPdfUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import com.example.traveldiary.domain.usecase.GetProfileUseCase
import com.example.traveldiary.domain.usecase.ToggleFavouriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import java.io.File
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val rule = TestDispatcherRule()

    private val getEntriesUseCase: GetEntriesUseCase = mockk()
    private val exportEntriesUseCase: ExportEntriesUseCase = mockk()
    private val exportPdfUseCase: ExportPdfUseCase = mockk()
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase = mockk(relaxed = true)
    private val getProfileUseCase: GetProfileUseCase = mockk()

    private fun entry(
        id: Int,
        title: String,
        country: String,
        isFavourite: Boolean = false,
        lat: Double? = null,
        lon: Double? = null,
    ) = TravelEntry(
        id = id, title = title, location = "L", country = country,
        tag = "X", imageUrl = "url", isFavourite = isFavourite,
        latitude = lat, longitude = lon,
    )

    private fun viewModelWith(entries: List<TravelEntry>): HomeViewModel {
        every { getEntriesUseCase() } returns flowOf(entries)
        every { getProfileUseCase() } returns flowOf(UserProfile())
        return HomeViewModel(
            getEntriesUseCase,
            exportEntriesUseCase,
            exportPdfUseCase,
            toggleFavouriteUseCase,
            getProfileUseCase,
        )
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
    fun `entriesList emits all entries unfiltered`() =
        runTest {
            val entries =
                listOf(
                    entry(1, "Paris", "France"),
                    entry(2, "Tokyo", "Japan"),
                )
            val viewModel = viewModelWith(entries)
            backgroundScope.launch { viewModel.entriesList.collect {} }
            advanceUntilIdle()

            assertEquals(listOf("Paris", "Tokyo"), viewModel.entriesList.value.map { it.title })
        }

    @Test
    fun `favouritesOnly filters entriesList`() =
        runTest {
            val entries =
                listOf(
                    entry(1, "Paris", "France", isFavourite = true),
                    entry(2, "Tokyo", "Japan"),
                )
            val viewModel = viewModelWith(entries)
            backgroundScope.launch { viewModel.entriesList.collect {} }
            advanceUntilIdle()

            viewModel.onFavouritesOnlyChanged(true)
            advanceUntilIdle()

            assertEquals(listOf("Paris"), viewModel.entriesList.value.map { it.title })

            viewModel.onFavouritesOnlyChanged(false)
            advanceUntilIdle()

            assertEquals(2, viewModel.entriesList.value.size)
        }

    @Test
    fun `toggleFavourite calls use case`() =
        runTest {
            val viewModel = viewModelWith(emptyList())
            viewModel.toggleFavourite(7, false)
            advanceUntilIdle()
            coVerify { toggleFavouriteUseCase(7, false) }
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
    fun `selectAll selects all visible entries`() =
        runTest {
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
    fun `exportSelectedTo emits Success and exits selection`() =
        runTest {
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
    fun `exportSelectedTo emits Error on failure`() =
        runTest {
            coEvery { exportEntriesUseCase.saveToUri(any(), any(), any()) } throws IOException("boom")
            val viewModel = viewModelWith(emptyList())
            viewModel.toggleSelection(1)

            viewModel.exportSelectedTo("content://d", "x.zip")
            advanceUntilIdle()

            assertEquals(ExportUiState.Error("boom"), viewModel.exportState.value)
        }

    @Test
    fun `shareSelected emits ShareReady and exits selection`() =
        runTest {
            coEvery { exportEntriesUseCase.toTempZip(any(), any()) } returns File("export.zip")
            val viewModel = viewModelWith(emptyList())
            viewModel.enterSelectionMode()
            viewModel.toggleSelection(2)

            viewModel.shareSelected()
            advanceUntilIdle()

            assertEquals(
                ExportUiState.ShareReady("export.zip", "application/zip"),
                viewModel.exportState.value,
            )
            assertFalse(viewModel.selectionMode.value)
            coVerify { exportEntriesUseCase.toTempZip(setOf(2), any()) }
        }

    @Test
    fun `exportPdfSelectedTo calls pdf use case and emits Success`() =
        runTest {
            coEvery { exportPdfUseCase.saveToUri(any(), any()) } returns Unit
            val viewModel = viewModelWith(emptyList())
            viewModel.enterSelectionMode()
            viewModel.toggleSelection(5)

            viewModel.exportPdfSelectedTo("content://d", "diario.pdf")
            advanceUntilIdle()

            assertEquals(ExportUiState.Success("diario.pdf"), viewModel.exportState.value)
            assertFalse(viewModel.selectionMode.value)
            coVerify { exportPdfUseCase.saveToUri(setOf(5), "content://d") }
        }

    @Test
    fun `sharePdfSelected emits ShareReady with pdf mime`() =
        runTest {
            coEvery { exportPdfUseCase.toTempPdf(any()) } returns File("diario.pdf")
            val viewModel = viewModelWith(emptyList())
            viewModel.enterSelectionMode()
            viewModel.toggleSelection(5)

            viewModel.sharePdfSelected()
            advanceUntilIdle()

            assertEquals(
                ExportUiState.ShareReady("diario.pdf", "application/pdf"),
                viewModel.exportState.value,
            )
            assertFalse(viewModel.selectionMode.value)
            coVerify { exportPdfUseCase.toTempPdf(setOf(5)) }
        }

    @Test
    fun `onExportStateConsumed resets to Idle`() =
        runTest {
            coEvery { exportEntriesUseCase.toTempZip(any(), any()) } returns File("export.zip")
            val viewModel = viewModelWith(emptyList())
            viewModel.toggleSelection(2)
            viewModel.shareSelected()
            advanceUntilIdle()

            viewModel.onExportStateConsumed()

            assertEquals(ExportUiState.Idle, viewModel.exportState.value)
        }
}
