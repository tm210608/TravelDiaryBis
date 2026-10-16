package com.example.traveldiary.domain.export

import java.io.InputStream

interface ImportStorage {
    fun openInputStream(sourceUri: String): InputStream?
    fun savePhoto(fileName: String, bytes: ByteArray): String?
}
