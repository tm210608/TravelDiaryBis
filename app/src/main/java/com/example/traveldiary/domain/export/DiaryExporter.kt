package com.example.traveldiary.domain.export

import com.example.traveldiary.domain.model.TravelEntry
import java.io.OutputStream
import java.time.Instant

interface DiaryExporter {
    suspend fun export(
        entries: List<TravelEntry>,
        appVersion: String,
        photoFor: (String) -> LoadedPhoto?,
        out: OutputStream,
        exportedAt: Instant = Instant.now(),
    )
}
