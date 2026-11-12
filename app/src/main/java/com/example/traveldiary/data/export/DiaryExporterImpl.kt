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
                    val (photoFile, extraPhotos) = writePhotos(zip, entry, photoFor)
                    exported += entry.toExported(photoFile, extraPhotos)
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

        private fun writePhotos(
            zip: ZipOutputStream,
            entry: TravelEntry,
            photoFor: (String) -> LoadedPhoto?,
        ): Pair<String?, List<String>> {
            fun write(
                uri: String,
                index: Int,
            ): String? {
                if (uri.isBlank() || uri.startsWith("http")) return null
                val photo = photoFor(uri) ?: return null
                val photoFile = "photos/entry-${entry.id}-$index.${photo.extension}"
                zip.putNextEntry(ZipEntry(photoFile))
                zip.write(photo.bytes)
                zip.closeEntry()
                return photoFile
            }
            val cover = write(entry.imageUrl, 0)
            val rest = entry.photoUris.mapIndexedNotNull { index, uri -> write(uri, index + 1) }
            return cover to rest
        }

        private fun TravelEntry.toExported(
            photoFile: String?,
            photoFiles: List<String>,
        ) =
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
                photoFiles = photoFiles,
            )

        companion object {
            const val DIARY_JSON_ENTRY = "diary.json"
        }
    }
