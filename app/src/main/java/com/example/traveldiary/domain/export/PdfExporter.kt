package com.example.traveldiary.domain.export

import com.example.traveldiary.domain.model.PdfCoverData
import com.example.traveldiary.domain.model.TravelEntry
import java.io.OutputStream
import java.time.Instant

interface PdfExporter {
    suspend fun export(
        entries: List<TravelEntry>,
        cover: PdfCoverData,
        photoFor: (String) -> LoadedPhoto?,
        out: OutputStream,
        exportedAt: Instant = Instant.now(),
    )
}
