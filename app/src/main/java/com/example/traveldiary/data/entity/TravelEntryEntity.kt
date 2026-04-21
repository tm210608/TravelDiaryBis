package com.example.traveldiary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Room Entity for travel entries.
 */
@Entity(tableName = "travel_entries")
data class TravelEntryEntity(
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
