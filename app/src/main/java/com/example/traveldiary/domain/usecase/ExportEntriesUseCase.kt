package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.export.DiaryExporter
import com.example.traveldiary.domain.export.ExportStorage
import com.example.traveldiary.domain.export.PhotoLoader
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.exportFileName
import com.example.traveldiary.domain.repository.TravelRepository
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ExportEntriesUseCase @Inject constructor(
    private val repository: TravelRepository,
    private val exporter: DiaryExporter,
    private val photoLoader: PhotoLoader,
    private val storage: ExportStorage,
) {
    suspend fun saveToUri(ids: Set<Int>, destinationUri: String, appVersion: String) =
        withContext(Dispatchers.IO) {
            val stream = storage.openOutputStream(destinationUri)
                ?: throw IOException("No se pudo abrir el destino de exportación")
            stream.use { exporter.export(selectedEntries(ids), appVersion, photoLoader::load, it) }
        }

    suspend fun toTempZip(ids: Set<Int>, appVersion: String): File =
        withContext(Dispatchers.IO) {
            val file = storage.createTempZipFile(exportFileName())
            file.outputStream().use {
                exporter.export(selectedEntries(ids), appVersion, photoLoader::load, it)
            }
            file
        }

    private suspend fun selectedEntries(ids: Set<Int>): List<TravelEntry> =
        repository.getAllEntriesStream().first().filter { it.id in ids }
}
