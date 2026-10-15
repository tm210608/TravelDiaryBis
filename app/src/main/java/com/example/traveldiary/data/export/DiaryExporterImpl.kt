package com.example.traveldiary.data.export

import com.example.traveldiary.domain.export.DiaryExporter
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.model.ExportDocument
import com.example.traveldiary.domain.model.ExportedEntry
import com.example.traveldiary.domain.model.TravelEntry
import java.io.OutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DiaryExporterImpl @Inject constructor() : DiaryExporter {

    private val json = Json { prettyPrint = true }

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
                var photoFile: String? = null
                if (entry.imageUrl.isNotBlank() && !entry.imageUrl.startsWith("http")) {
                    photoFor(entry.imageUrl)?.let { photo ->
                        photoFile = "photos/entry-${entry.id}-0.${photo.extension}"
                        zip.putNextEntry(ZipEntry(photoFile))
                        zip.write(photo.bytes)
                        zip.closeEntry()
                    }
                }
                exported += entry.toExported(photoFile)
            }
            val document = ExportDocument(
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

    private fun TravelEntry.toExported(photoFile: String?) = ExportedEntry(
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
    )

    companion object {
        const val DIARY_JSON_ENTRY = "diary.json"
    }
}
