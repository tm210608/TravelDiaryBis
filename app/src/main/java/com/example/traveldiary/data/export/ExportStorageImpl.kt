package com.example.traveldiary.data.export

import android.content.Context
import android.net.Uri
import com.example.traveldiary.domain.export.ExportStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

class ExportStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExportStorage {

    override fun openOutputStream(destinationUri: String): OutputStream? =
        context.contentResolver.openOutputStream(Uri.parse(destinationUri))

    override fun createTempZipFile(fileName: String): File =
        File(File(context.cacheDir, "exports").apply { mkdirs() }, fileName)
}
