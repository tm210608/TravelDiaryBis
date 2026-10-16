package com.example.traveldiary.domain.usecase

import com.example.traveldiary.data.export.DiaryExporterImpl
import com.example.traveldiary.domain.export.ExportStorage
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.export.PhotoLoader
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.every
import io.mockk.mockk
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.ZipInputStream
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportEntriesUseCaseTest {

    private val repository: TravelRepository = mockk()
    private val storage: ExportStorage = mockk()
    private val photoLoader = PhotoLoader { LoadedPhoto(byteArrayOf(9), "jpg") }
    private val useCase = ExportEntriesUseCase(repository, DiaryExporterImpl(), photoLoader, storage)
    private val json = Json { ignoreUnknownKeys = true }

    private val entries = listOf(
        TravelEntry(id = 1, title = "A", location = "L", country = "C", tag = "t", imageUrl = "file:///a.jpg"),
        TravelEntry(id = 2, title = "B", location = "L", country = "C", tag = "t", imageUrl = "file:///b.jpg"),
        TravelEntry(id = 3, title = "C", location = "L", country = "C", tag = "t", imageUrl = ""),
    )

    private fun documentFrom(bytes: ByteArray): ExportDocument {
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var e = zip.nextEntry
            while (e != null) {
                if (e.name == "diary.json") {
                    return json.decodeFromString(zip.readBytes().toString(Charsets.UTF_8))
                }
                zip.closeEntry()
                e = zip.nextEntry
            }
        }
        error("diary.json not found")
    }

    @Test
    fun `saveToUri exports only selected ids`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        val out = ByteArrayOutputStream()
        every { storage.openOutputStream("content://dest") } returns out

        useCase.saveToUri(setOf(2), "content://dest", "1.0")

        val doc = documentFrom(out.toByteArray())
        assertEquals(1, doc.entryCount)
        assertEquals(2, doc.entries[0].id)
    }

    @Test
    fun `saveToUri throws when destination cannot be opened`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        every { storage.openOutputStream("content://dest") } returns null

        assertThrows(IOException::class.java) {
            kotlinx.coroutines.runBlocking { useCase.saveToUri(setOf(1), "content://dest", "1.0") }
        }
    }

    @Test
    fun `toTempZip writes zip file to storage`() = runTest {
        every { repository.getAllEntriesStream() } returns flowOf(entries)
        val temp = File.createTempFile("export-test", ".zip").apply { deleteOnExit() }
        every { storage.createTempZipFile(any()) } returns temp

        val file = useCase.toTempZip(setOf(1, 3), "1.0")

        assertEquals(temp, file)
        assertTrue(file.length() > 0)
        val doc = documentFrom(file.readBytes())
        assertEquals(listOf(1, 3), doc.entries.map { it.id })
        assertEquals("photos/entry-1-0.jpg", doc.entries[0].photoFile)
        assertEquals(null, doc.entries[1].photoFile)
    }
}
