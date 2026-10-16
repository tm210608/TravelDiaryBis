package com.example.traveldiary.domain.export

import java.io.InputStream

interface DiaryImporter {
    suspend fun import(
        input: InputStream,
        savePhoto: (fileName: String, bytes: ByteArray) -> String?,
    ): ImportResult
}
