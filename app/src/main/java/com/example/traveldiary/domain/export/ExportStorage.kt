package com.example.traveldiary.domain.export

import java.io.File
import java.io.OutputStream

interface ExportStorage {
    fun openOutputStream(destinationUri: String): OutputStream?
    fun createTempZipFile(fileName: String): File
}
