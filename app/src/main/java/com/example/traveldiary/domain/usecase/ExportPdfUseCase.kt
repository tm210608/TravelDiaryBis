package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.export.ExportStorage
import com.example.traveldiary.domain.export.PdfExporter
import com.example.traveldiary.domain.export.PhotoLoader
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.buildPdfCover
import com.example.traveldiary.domain.model.exportPdfFileName
import com.example.traveldiary.domain.repository.TravelRepository
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ExportPdfUseCase @Inject constructor(
    private val repository: TravelRepository,
    private val pdfExporter: PdfExporter,
    private val photoLoader: PhotoLoader,
    private val storage: ExportStorage,
) {
    suspend fun saveToUri(ids: Set<Int>, destinationUri: String) =
        withContext(Dispatchers.IO) {
            val stream = storage.openOutputStream(destinationUri)
                ?: throw IOException("No se pudo abrir el destino de exportación")
            val entries = selectedEntries(ids)
            stream.use { pdfExporter.export(entries, buildPdfCover(entries), photoLoader::load, it) }
        }

    suspend fun toTempPdf(ids: Set<Int>): File =
        withContext(Dispatchers.IO) {
            val file = storage.createTempExportFile(exportPdfFileName())
            val entries = selectedEntries(ids)
            file.outputStream().use {
                pdfExporter.export(entries, buildPdfCover(entries), photoLoader::load, it)
            }
            file
        }

    private suspend fun selectedEntries(ids: Set<Int>): List<TravelEntry> =
        repository.getAllEntriesStream().first()
            .filter { it.id in ids }
            .sortedBy { it.date }
}
