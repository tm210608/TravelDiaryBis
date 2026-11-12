package com.example.traveldiary.domain.repository

interface PhotoImporter {
    suspend fun importPhotos(sourceUris: List<String>): Result

    suspend fun importPhoto(sourceUri: String): String?

    data class Result(
        val imported: List<String>,
        val failedCount: Int,
    )
}
