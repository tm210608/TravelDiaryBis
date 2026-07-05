package com.example.traveldiary.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.formatToDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

@Entity(tableName = "travel_entries")
data class TravelEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val location: String,
    val country: String,
    val date: Instant = Instant.now(),
    val tag: String,
    val imageUrl: String,
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFavourite: Boolean = false,
)
