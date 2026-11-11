package com.example.traveldiary.domain.model

import java.time.ZoneId

data class PdfCoverData(
    val trips: Int,
    val favourites: Int,
    val countries: Int,
    val continents: Int,
    val kmTotal: Double,
    val legs: Int,
    val daysOnRoute: Int,
)

fun buildPdfCover(
    entries: List<TravelEntry>,
    zone: ZoneId = ZoneId.systemDefault(),
): PdfCoverData {
    val (km, legs) = totalDistanceKm(entries)
    return PdfCoverData(
        trips = entries.size,
        favourites = entries.count { it.isFavourite },
        countries = entries.map { it.country.trim() }.filter { it.isNotEmpty() }.distinct().size,
        continents = entries.mapNotNull { continentOf(it.country) }.distinct().size,
        kmTotal = km,
        legs = legs,
        daysOnRoute = daysWithEntries(entries, zone),
    )
}
