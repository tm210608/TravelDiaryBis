package com.example.traveldiary.domain.model

import java.time.Instant
import java.util.Locale

data class PassportData(
    val kmTotal: Double,
    val legs: Int,
    val stamps: List<RouteStamp>,
)

data class RouteStamp(
    val code: String,
    val label: String,
    val firstVisit: Instant,
    val rotation: Float,
)

fun buildRouteStamps(entries: List<TravelEntry>, maxStamps: Int = 8): List<RouteStamp> =
    entries
        .filter { it.country.isNotBlank() }
        .groupBy { it.country.normalized() }
        .map { (norm, visits) ->
            RouteStamp(
                code = countryCodeOf(norm)!!,
                label = visits.first().country.trim().uppercase(Locale.ROOT),
                firstVisit = visits.minOf { it.date },
                rotation = stampRotation(norm),
            )
        }
        .sortedByDescending { it.firstVisit }
        .take(maxStamps)

private fun stampRotation(key: String): Float {
    val bucket = (key.hashCode() and Int.MAX_VALUE) % 5
    return -6f + 12f * bucket / 4f
}
