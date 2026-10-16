package com.example.traveldiary.data.export

import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.TravelEntry
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util.zip.ZipInputStream
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiaryExporterImplTest {

    private val exporter = DiaryExporterImpl()
    private val json = Json { ignoreUnknownKeys = true }
    private val exportedAt = Instant.parse("2026-10-15T10:00:00Z")

    private fun entry(id: Int, imageUrl: String) = TravelEntry(
        id = id,
        title = "T$id",
        location = "L$id",
        country = "C$id",
        date = Instant.parse("2026-01-0${id}T12:00:00Z"),
        tag = "tag",
        imageUrl = imageUrl,
        description = "d$id",
        latitude = 1.5,
        longitude = 2.5,
        isFavourite = true,
    )

    private fun unzip(bytes: ByteArray): Map<String, ByteArray> {
        val files = mutableMapOf<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var e = zip.nextEntry
            while (e != null) {
                files[e.name] = zip.readBytes()
                zip.closeEntry()
                e = zip.nextEntry
            }
        }
        return files
    }

    private suspend fun exportToMap(
        entries: List<TravelEntry>,
        photoFor: (String) -> LoadedPhoto? = { null },
    ): Map<String, ByteArray> {
        val out = ByteArrayOutputStream()
        exporter.export(entries, "1.0", photoFor, out, exportedAt)
        return unzip(out.toByteArray())
    }

    private fun Map<String, ByteArray>.diaryJson(): ExportDocument =
        json.decodeFromString(getValue(DiaryExporterImpl.DIARY_JSON_ENTRY).toString(Charsets.UTF_8))

    @Test
    fun `export writes diary json and photo entries`() = runTest {
        val entries = listOf(entry(1, "file:///p1.jpg"), entry(2, "file:///p2.png"))
        val files = exportToMap(entries) { LoadedPhoto(byteArrayOf(1, 2, 3), "jpg") }

        assertEquals(setOf("photos/entry-1-0.jpg", "photos/entry-2-0.jpg", "diary.json"), files.keys)
        assertEquals(listOf<Byte>(1, 2, 3), files.getValue("photos/entry-1-0.jpg").toList())

        val rawJson = files.getValue(DiaryExporterImpl.DIARY_JSON_ENTRY).toString(Charsets.UTF_8)
        assertTrue(rawJson.contains("\"formatVersion\": 1"))
        assertTrue(rawJson.contains("\"app\": \"TravelDiaryBis\""))

        val doc = files.diaryJson()
        assertEquals(1, doc.formatVersion)
        assertEquals("TravelDiaryBis", doc.app)
        assertEquals("1.0", doc.appVersion)
        assertEquals("2026-10-15T10:00:00Z", doc.exportedAt)
        assertEquals(2, doc.entryCount)
        assertEquals("photos/entry-1-0.jpg", doc.entries[0].photoFile)
        assertEquals("2026-01-01T12:00:00Z", doc.entries[0].date)
        assertEquals(1.5, doc.entries[0].latitude)
        assertEquals(true, doc.entries[0].isFavourite)
    }

    @Test
    fun `http imageUrl skips photo and keeps null photoFile`() = runTest {
        var loaderCalled = false
        val files = exportToMap(listOf(entry(1, "https://img.example.com/x.jpg"))) {
            loaderCalled = true
            LoadedPhoto(byteArrayOf(1), "jpg")
        }

        assertFalse(loaderCalled)
        assertNull(files.diaryJson().entries[0].photoFile)
        assertEquals(setOf("diary.json"), files.keys)
        assertEquals("https://img.example.com/x.jpg", files.diaryJson().entries[0].imageUrl)
    }

    @Test
    fun `unreadable photo keeps entry with null photoFile`() = runTest {
        val files = exportToMap(listOf(entry(1, "file:///gone.jpg"))) { null }

        assertEquals(setOf("diary.json"), files.keys)
        assertNull(files.diaryJson().entries[0].photoFile)
        assertEquals(1, files.diaryJson().entryCount)
    }

    @Test
    fun `empty selection exports document with zero entries`() = runTest {
        val files = exportToMap(emptyList())

        assertEquals(0, files.diaryJson().entryCount)
        assertTrue(files.diaryJson().entries.isEmpty())
    }
}
