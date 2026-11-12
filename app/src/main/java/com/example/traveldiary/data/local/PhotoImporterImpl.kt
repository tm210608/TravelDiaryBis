package com.example.traveldiary.data.local

import android.content.Context
import android.net.Uri
import com.example.traveldiary.domain.repository.PhotoImporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class PhotoImporterImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : PhotoImporter {
        override suspend fun importPhotos(sourceUris: List<String>): PhotoImporter.Result =
            withContext(Dispatchers.IO) {
                val imported = mutableListOf<String>()
                var failed = 0
                sourceUris.forEach { uri ->
                    val copied = copyToPhotosDir(uri)
                    if (copied != null) imported.add(copied) else failed++
                }
                PhotoImporter.Result(imported = imported, failedCount = failed)
            }

        override suspend fun importPhoto(sourceUri: String): String? =
            withContext(Dispatchers.IO) {
                copyToPhotosDir(sourceUri)
            }

        private fun copyToPhotosDir(sourceUri: String): String? {
            return try {
                val uri = Uri.parse(sourceUri)
                val extension =
                    context.contentResolver.getType(uri)
                        ?.substringAfterLast('/')
                        ?.takeIf { it.isNotBlank() }
                        ?: "jpg"
                val photoDir = File(context.filesDir, "photos").apply { mkdirs() }
                val dest = File(photoDir, "${UUID.randomUUID()}.$extension")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    dest.outputStream().use { output -> input.copyTo(output) }
                } ?: return null
                Uri.fromFile(dest).toString()
            } catch (_: Exception) {
                null
            }
        }
    }
