package com.example.traveldiary.domain.model

import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import kotlin.math.abs

private val MONTHS_ES = arrayOf(
    "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
    "JUL", "AGO", "SEP", "OCT", "NOV", "DIC",
)

fun formatToCardinal(value: Double, positive: Char, negative: Char): String {
    val cardinal = if (value < 0) negative else positive
    return String.format(Locale.US, "%.4f\u00b0%c", abs(value), cardinal)
}

fun Double.toCardinalLat(): String = formatToCardinal(this, 'N', 'S')

fun Double.toCardinalLon(): String = formatToCardinal(this, 'E', 'W')

val TravelEntry.hasGps: Boolean
    get() = latitude != null && longitude != null

fun TravelEntry.telemetryLine(): String =
    if (hasGps) "${latitude!!.toCardinalLat()} \u00b7 ${longitude!!.toCardinalLon()}"
    else "$location, $country"

data class TicketDate(
    val day: String,
    val month: String,
    val year: String,
    val yearShort: String,
    val time: String,
)

fun Instant.formatToTicket(zone: ZoneId = ZoneId.systemDefault()): TicketDate {
    val d = atZone(zone)
    return TicketDate(
        day = String.format(Locale.US, "%02d", d.dayOfMonth),
        month = MONTHS_ES[d.monthValue - 1],
        year = String.format(Locale.US, "%04d", d.year),
        yearShort = String.format(Locale.US, "%02d", d.year % 100),
        time = String.format(Locale.US, "%02d:%02d", d.hour, d.minute),
    )
}

fun Instant.formatToCompact(zone: ZoneId = ZoneId.systemDefault()): String {
    val t = formatToTicket(zone)
    return "${t.day}\u00b7${t.month}\u00b7${t.yearShort}"
}
