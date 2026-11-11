package com.example.traveldiary.data.export

import com.example.traveldiary.domain.export.DiaryExporter
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.ExportedEntry
import com.example.traveldiary.domain.model.TravelEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class DiaryExporterImpl
    @Inject
    constructor() : DiaryExporter {
        private val json =
            Json {
                prettyPrint = true
                encodeDefaults = true
            }

        override suspend fun export(
            entries: List<TravelEntry>,
            appVersion: String,
            photoFor: (String) -> LoadedPhoto?,
            out: OutputStream,
            exportedAt: Instant,
        ) {
            val exported = mutableListOf<ExportedEntry>()
            ZipOutputStream(out.buffered()).use { zip ->
                entries.forEach { entry ->
                    val photoFile = writePhoto(zip, entry, photoFor)
                    exported += entry.toExported(photoFile)
                }
                val document =
                    ExportDocument(
                        appVersion = appVersion,
                        exportedAt = exportedAt.toString(),
                        entryCount = exported.size,
                        entries = exported,
                    )
                zip.putNextEntry(ZipEntry(DIARY_JSON_ENTRY))
                zip.write(json.encodeToString(document).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        }

        private fun writePhoto(
            zip: ZipOutputStream,
            entry: TravelEntry,
            photoFor: (String) -> LoadedPhoto?,
        ): String? {
            if (entry.imageUrl.isBlank() || entry.imageUrl.startsWith("http")) return null
            val photo = photoFor(entry.imageUrl) ?: return null
            val photoFile = "photos/entry-${entry.id}-0.${photo.extension}"
            zip.putNextEntry(ZipEntry(photoFile))
            zip.write(photo.bytes)
            zip.closeEntry()
            return photoFile
        }

        private fun TravelEntry.toExported(photoFile: String?) =
            ExportedEntry(
                id = id,
                title = title,
                location = location,
                country = country,
                date = date.toString(),
                tag = tag,
                description = description,
                latitude = latitude,
                longitude = longitude,
                isFavourite = isFavourite,
                imageUrl = imageUrl,
                photoFile = photoFile,
                uuid = uuid,
            )

        companion object {
            const val DIARY_JSON_ENTRY = "diary.json"
        }
    }
