package com.example.traveldiary.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun exportFileName(now: Instant = Instant.now()): String {
    val formatter =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")
            .withZone(ZoneId.systemDefault())
    return "traveldiary-export-${formatter.format(now)}.zip"
}

fun exportPdfFileName(now: Instant = Instant.now()): String {
    val formatter =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")
            .withZone(ZoneId.systemDefault())
    return "traveldiary-diario-${formatter.format(now)}.pdf"
}

fun shareMimeType(fileName: String): String =
    when {
        fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
        fileName.endsWith(".zip", ignoreCase = true) -> "application/zip"
        else -> "application/octet-stream"
    }

@Serializable
data class ExportDocument(
    val formatVersion: Int = 1,
    val app: String = "TravelDiaryBis",
    val appVersion: String,
    val exportedAt: String,
    val entryCount: Int,
    val entries: List<ExportedEntry>,
)

@Serializable
data class ExportedEntry(
    val id: Int,
    val title: String,
    val location: String,
    val country: String,
    val date: String,
    val tag: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val isFavourite: Boolean,
    val imageUrl: String,
    val photoFile: String?,
    val uuid: String = "",
)
