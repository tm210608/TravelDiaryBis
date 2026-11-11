package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.export.ExportStorage
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.export.PdfExporter
import com.example.traveldiary.domain.export.PhotoLoader
import com.example.traveldiary.domain.model.PdfCoverData
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.time.Instant
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportPdfUseCaseTest {

    private class FakePdfExporter : PdfExporter {
        var entries: List<TravelEntry> = emptyList()
        var cover: PdfCoverData? = null

        override suspend fun export(
            entries: List<TravelEntry>,
            cover: PdfCoverData,
            photoFor: (String) -> LoadedPhoto?,
            out: OutputStream,
            exportedAt: Instant,
        ) {
            this.entries = entries
            this.cover = cover
            out.write(byteArrayOf(1))
        }
    }

    private val repository: TravelRepository = mockk()
    private val storage: ExportStorage = mockk()
    private val photoLoader = PhotoLoader { LoadedPhoto(byteArrayOf(9), "jpg") }
    private val exporter = FakePdfExporter()
    private val useCase = ExportPdfUseCase(repository, exporter, photoLoader, storage)

    private val entries = listOf(
        TravelEntry(id = 1, title = "Nueva", location = "L", country = "C", tag = "t",
            imageUrl = "", date = Instant.parse("2026-11-10T10:00:00Z")),
        TravelEntry(id = 2, title = "Vieja", location = "L", country = "C", tag = "t",
            imageUrl = "", date = Instant.parse("2026-11-05T10:00:00Z")),
        TravelEntry(id = 3, title = "Media", location = "L", country = "C", tag = "t",
            imageUrl = "", date = Instant.parse("2026-11-08T10:00:00Z")),
    )

    @Test
    fun `saveToUri exports only selected ids sorted by date ascending`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        val out = ByteArrayOutputStream()
        every { storage.openOutputStream("content://dest") } returns out

        useCase.saveToUri(setOf(1, 2, 3), "content://dest")

        assertEquals(listOf(2, 3, 1), exporter.entries.map { it.id })
        assertEquals(3, exporter.cover?.trips)
    }

    @Test
    fun `saveToUri throws when destination cannot be opened`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        every { storage.openOutputStream("content://dest") } returns null

        assertThrows(IOException::class.java) {
            kotlinx.coroutines.runBlocking { useCase.saveToUri(setOf(1), "content://dest") }
        }
    }

    @Test
    fun `toTempPdf writes pdf file to storage`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        val temp = File.createTempFile("export-test", ".pdf").apply { deleteOnExit() }
        every { storage.createTempExportFile(any()) } returns temp

        val file = useCase.toTempPdf(setOf(1, 3))

        assertEquals(temp, file)
        assertTrue(file.length() > 0)
        assertEquals(listOf(3, 1), exporter.entries.map { it.id })
        assertEquals(2, exporter.cover?.trips)
    }
}
