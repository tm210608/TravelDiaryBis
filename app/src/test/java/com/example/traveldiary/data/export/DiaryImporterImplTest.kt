package com.example.traveldiary.data.export

import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.ExportedEntry
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiaryImporterImplTest {

    private val importer = DiaryImporterImpl()
    private val json = Json { encodeDefaults = true }

    private fun exported(
        id: Int,
        photoFile: String?,
        imageUrl: String = "orig://$id",
    ) = ExportedEntry(
        id = id,
        title = "T$id",
        location = "L$id",
        country = "C$id",
        date = "2026-01-0${id}T12:00:00Z",
        tag = "tag",
        description = "d$id",
        latitude = 1.5,
        longitude = 2.5,
        isFavourite = true,
        imageUrl = imageUrl,
        photoFile = photoFile,
    )

    private fun zipOf(vararg files: Pair<String, ByteArray>): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            files.forEach { (name, bytes) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
        return out.toByteArray()
    }

    private fun docJson(entries: List<ExportedEntry>, version: Int = 1): ByteArray =
        json.encodeToString(
            ExportDocument(
                formatVersion = version,
                appVersion = "1.0",
                exportedAt = "2026-10-15T10:00:00Z",
                entryCount = entries.size,
                entries = entries,
            )
        ).toByteArray()

    private suspend fun import(zip: ByteArray): Pair<ImportResult, Map<String, ByteArray>> {
        val saved = mutableMapOf<String, ByteArray>()
        val result = importer.import(ByteArrayInputStream(zip)) { name, bytes ->
            saved[name] = bytes
            "file:///new/$name"
        }
        return result to saved
    }

    @Test
    fun `import parses document and rewrites photos`() = runTest {
        val zip = zipOf(
            "photos/entry-1-0.jpg" to byteArrayOf(1, 2, 3),
            "diary.json" to docJson(listOf(exported(1, "photos/entry-1-0.jpg"), exported(2, null))),
        )

        val (result, saved) = import(zip)

        assertTrue(result is ImportResult.Success)
        val entries = (result as ImportResult.Success).entries
        assertEquals(2, entries.size)
        assertEquals(0, entries[0].id)
        assertEquals("file:///new/entry-1-0.jpg", entries[0].imageUrl)
        assertEquals("orig://2", entries[1].imageUrl)
        assertEquals("2026-01-01T12:00:00Z", entries[0].date.toString())
        assertEquals("T1", entries[0].title)
        assertEquals("d1", entries[0].description)
        assertEquals(1.5, entries[0].latitude)
        assertEquals(true, entries[0].isFavourite)
        assertEquals(listOf<Byte>(1, 2, 3), saved.getValue("entry-1-0.jpg").toList())
    }

    @Test
    fun `photoFile missing from zip keeps original imageUrl`() = runTest {
        val zip = zipOf("diary.json" to docJson(listOf(exported(1, "photos/gone.jpg"))))

        val (result, _) = import(zip)

        val entries = (result as ImportResult.Success).entries
        assertEquals("orig://1", entries[0].imageUrl)
    }

    @Test
    fun `non zip input returns InvalidFile`() = runTest {
        val (result, _) = import("not a zip".toByteArray())
        assertEquals(ImportResult.InvalidFile, result)
    }

    @Test
    fun `zip without diary json returns InvalidFile`() = runTest {
        val zip = zipOf("photos/a.jpg" to byteArrayOf(1))
        val (result, _) = import(zip)
        assertEquals(ImportResult.InvalidFile, result)
    }

    @Test
    fun `malformed json returns InvalidFile`() = runTest {
        val zip = zipOf("diary.json" to "{broken".toByteArray())
        val (result, _) = import(zip)
        assertEquals(ImportResult.InvalidFile, result)
    }

    @Test
    fun `formatVersion 2 returns UnsupportedVersion`() = runTest {
        val zip = zipOf("diary.json" to docJson(listOf(exported(1, null)), version = 2))
        val (result, _) = import(zip)
        assertEquals(ImportResult.UnsupportedVersion, result)
    }

    @Test
    fun `empty entries returns Empty`() = runTest {
        val zip = zipOf("diary.json" to docJson(emptyList()))
        val (result, _) = import(zip)
        assertEquals(ImportResult.Empty, result)
    }
}
