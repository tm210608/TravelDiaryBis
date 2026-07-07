package com.example.traveldiary.data.mapper

import com.example.traveldiary.data.local.TravelEntryEntity
import com.example.traveldiary.domain.model.TravelEntry

fun TravelEntryEntity.toDomain(): TravelEntry = TravelEntry(
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
    isFavourite = isFavourite,
)

fun TravelEntry.toEntity(): TravelEntryEntity = TravelEntryEntity(
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
    isFavourite = isFavourite,
)

fun List<TravelEntryEntity>.toDomainList(): List<TravelEntry> = map { it.toDomain() }
