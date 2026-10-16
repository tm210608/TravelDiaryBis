package com.example.traveldiary.data.export

import android.content.Context
import android.net.Uri
import com.example.traveldiary.domain.export.ImportStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ImportStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ImportStorage {

    private val counter = AtomicInteger(0)

    override fun openInputStream(sourceUri: String): InputStream? =
        context.contentResolver.openInputStream(Uri.parse(sourceUri))

    override fun savePhoto(fileName: String, bytes: ByteArray): String? = runCatching {
        val dir = File(context.filesDir, "photos").apply { mkdirs() }
        val ext = fileName.substringAfterLast('.', "jpg")
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val file = File(dir, "import-$stamp-${counter.getAndIncrement()}.$ext")
        file.writeBytes(bytes)
        "file://${file.absolutePath}"
    }.getOrNull()
}
