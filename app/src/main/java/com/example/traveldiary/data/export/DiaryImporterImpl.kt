package com.example.traveldiary.data.export

import com.example.traveldiary.domain.export.DiaryImporter
import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.ExportedEntry
import com.example.traveldiary.domain.model.TravelEntry
import java.io.InputStream
import java.time.Instant
import java.util.zip.ZipInputStream
import javax.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DiaryImporterImpl @Inject constructor() : DiaryImporter {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun import(
        input: InputStream,
        savePhoto: (fileName: String, bytes: ByteArray) -> String?,
    ): ImportResult {
        val savedPhotos = mutableMapOf<String, String>()
        var documentBytes: ByteArray? = null

        runCatching {
            ZipInputStream(input.buffered()).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    when {
                        entry.isDirectory -> Unit
                        entry.name == DIARY_JSON_ENTRY -> documentBytes = zip.readBytes()
                        entry.name.startsWith(PHOTOS_PREFIX) -> {
                            val fileName = entry.name.substringAfterLast('/')
                            savePhoto(fileName, zip.readBytes())
                                ?.let { savedPhotos[entry.name] = it }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }.getOrElse { return ImportResult.InvalidFile }

        val document = documentBytes
            ?.let { runCatching { json.decodeFromString<ExportDocument>(it.toString(Charsets.UTF_8)) }.getOrNull() }
            ?: return ImportResult.InvalidFile

        if (document.formatVersion != SUPPORTED_FORMAT_VERSION) {
            return ImportResult.UnsupportedVersion
        }
        if (document.entries.isEmpty()) return ImportResult.Empty

        return ImportResult.Success(document.entries.map { it.toDomain(savedPhotos) })
    }

    private fun ExportedEntry.toDomain(savedPhotos: Map<String, String>): TravelEntry {
        val resolvedUrl = photoFile
            ?.let { savedPhotos[it] }
            ?: imageUrl
        return TravelEntry(
            id = 0,
            title = title,
            location = location,
            country = country,
            date = runCatching { Instant.parse(date) }.getOrDefault(Instant.now()),
            tag = tag,
            imageUrl = resolvedUrl,
            description = description,
            latitude = latitude,
            longitude = longitude,
            isFavourite = isFavourite,
        )
    }

    companion object {
        const val DIARY_JSON_ENTRY = DiaryExporterImpl.DIARY_JSON_ENTRY
        const val PHOTOS_PREFIX = "photos/"
        const val SUPPORTED_FORMAT_VERSION = 1
    }
}
