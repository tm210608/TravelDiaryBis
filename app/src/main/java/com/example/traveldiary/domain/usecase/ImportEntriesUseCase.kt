package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.export.DiaryImporter
import com.example.traveldiary.domain.export.ImportResult
import com.example.traveldiary.domain.export.ImportStorage
import com.example.traveldiary.domain.repository.TravelRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportEntriesUseCase @Inject constructor(
    private val repository: TravelRepository,
    private val importer: DiaryImporter,
    private val storage: ImportStorage,
) {
    suspend operator fun invoke(sourceUri: String): ImportResult =
        withContext(Dispatchers.IO) {
            val stream = storage.openInputStream(sourceUri)
                ?: return@withContext ImportResult.InvalidFile
            val result = stream.use { importer.import(it, storage::savePhoto) }
            if (result is ImportResult.Success) {
                result.entries.forEach { repository.insertEntry(it) }
            }
            result
        }
}
