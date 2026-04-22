package com.example.traveldiary.data.mapper

import com.example.traveldiary.data.entity.TravelEntryEntity
import com.example.traveldiary.domain.model.TravelEntry

/**
 * Mapper to convert between [TravelEntryEntity] and [TravelEntry].
 */
fun TravelEntryEntity.toDomain(): TravelEntry {
    return TravelEntry(
        id = id,
        title = title,
        location = location,
        country = country,
        date = date,
        tag = tag,
        imageUrl = imageUrl,
        description = description,
        latitude = latitude,
        longitude = longitude,
        isFavourite = isFavourite
    )
}

fun TravelEntry.toEntity(): TravelEntryEntity {
    return TravelEntryEntity(
        id = id,
        title = title,
        location = location,
        country = country,
        date = date,
        tag = tag,
        imageUrl = imageUrl,
        description = description,
        latitude = latitude,
        longitude = longitude,
        isFavourite = isFavourite
    )
}
