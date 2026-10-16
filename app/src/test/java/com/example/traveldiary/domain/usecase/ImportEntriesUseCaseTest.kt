package com.example.traveldiary.domain.usecase

import com.example.traveldiary.data.export.DiaryImporterImpl
import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.export.ImportStorage
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportEntriesUseCaseTest {

    private val repository: TravelRepository = mockk(relaxed = true)
    private val storage: ImportStorage = mockk()
    private val useCase = ImportEntriesUseCase(repository, DiaryImporterImpl(), storage)

    private fun validZip(): ByteArray {
        val doc = """
            {
              "formatVersion": 1, "app": "TravelDiaryBis", "appVersion": "1.0",
              "exportedAt": "2026-10-15T10:00:00Z", "entryCount": 2,
              "entries": [
                {"id": 7, "title": "A", "location": "L", "country": "C",
                 "date": "2026-01-01T12:00:00Z", "tag": "t", "description": "",
                 "latitude": null, "longitude": null, "isFavourite": false,
                 "imageUrl": "orig://a", "photoFile": null},
                {"id": 8, "title": "B", "location": "L", "country": "C",
                 "date": "2026-01-02T12:00:00Z", "tag": "t", "description": "",
                 "latitude": null, "longitude": null, "isFavourite": false,
                 "imageUrl": "orig://b", "photoFile": null}
              ]
            }
        """.trimIndent()
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            zip.putNextEntry(ZipEntry("diary.json"))
            zip.write(doc.toByteArray())
            zip.closeEntry()
        }
        return out.toByteArray()
    }

    @Test
    fun `invoke inserts every imported entry`() = runTest {
        every { storage.openInputStream("content://zip") } returns
            ByteArrayInputStream(validZip())
        every { storage.savePhoto(any(), any()) } returns "file:///new/x.jpg"

        val result = useCase("content://zip")

        assertTrue(result is ImportResult.Success)
        assertEquals(2, (result as ImportResult.Success).entries.size)
        coVerify(exactly = 2) { repository.insertEntry(any<TravelEntry>()) }
    }

    @Test
    fun `invoke returns InvalidFile when stream cannot be opened`() = runTest {
        every { storage.openInputStream("content://zip") } returns null

        val result = useCase("content://zip")

        assertEquals(ImportResult.InvalidFile, result)
        coVerify(exactly = 0) { repository.insertEntry(any<TravelEntry>()) }
    }
}
