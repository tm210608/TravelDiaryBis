package com.example.traveldiary.domain.model

import java.time.Instant

/**
 * Domain model representing a travel entry.
 * This is decoupled from the database entity.
 */
data class TravelEntry(
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
