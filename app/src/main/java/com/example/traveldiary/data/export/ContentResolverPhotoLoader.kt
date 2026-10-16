package com.example.traveldiary.data.export

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.export.PhotoLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ContentResolverPhotoLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) : PhotoLoader {

    override fun load(imageUrl: String): LoadedPhoto? = runCatching {
        when {
            imageUrl.startsWith("http://") || imageUrl.startsWith("https://") -> null
            imageUrl.startsWith("content://") -> loadFromUri(Uri.parse(imageUrl))
            imageUrl.startsWith("file://") -> Uri.parse(imageUrl).path?.let { File(it).toLoadedPhoto() }
            else -> File(imageUrl).toLoadedPhoto()
        }
    }.getOrNull()

    private fun loadFromUri(uri: Uri): LoadedPhoto? {
        val extension = context.contentResolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?: "jpg"
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return null
        return LoadedPhoto(bytes, extension)
    }

    private fun File.toLoadedPhoto(): LoadedPhoto? =
        takeIf { isFile }?.let { LoadedPhoto(it.readBytes(), extension.ifBlank { "jpg" }) }
}
