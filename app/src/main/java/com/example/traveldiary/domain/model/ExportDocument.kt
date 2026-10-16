package com.example.traveldiary.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.serialization.Serializable

fun exportFileName(now: Instant = Instant.now()): String {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")
        .withZone(ZoneId.systemDefault())
    return "traveldiary-export-${formatter.format(now)}.zip"
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
