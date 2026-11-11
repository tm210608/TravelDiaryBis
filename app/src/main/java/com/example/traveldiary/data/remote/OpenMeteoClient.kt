package com.example.traveldiary.data.remote

import com.example.traveldiary.domain.model.WeatherReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

private const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
private const val ARCHIVE_URL = "https://archive-api.open-meteo.com/v1/archive"
private const val FORECAST_PAST_DAYS = 92L
private const val TIMEOUT_MS = 5_000

class OpenMeteoClient
    @Inject
    constructor() {
        suspend fun fetch(
            latitude: Double,
            longitude: Double,
            instant: Instant,
        ): WeatherReading? =
            withContext(Dispatchers.IO) {
                val connection =
                    URL(openMeteoUrl(latitude, longitude, instant)).openConnection() as HttpURLConnection
                try {
                    connection.connectTimeout = TIMEOUT_MS
                    connection.readTimeout = TIMEOUT_MS
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    parseHourly(body, instant.epochSecond)
                } finally {
                    connection.disconnect()
                }
            }
    }

fun openMeteoUrl(
    latitude: Double,
    longitude: Double,
    instant: Instant,
    now: Instant = Instant.now(),
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val host =
        if (!instant.isBefore(now.minus(FORECAST_PAST_DAYS, ChronoUnit.DAYS))) {
            FORECAST_URL
        } else {
            ARCHIVE_URL
        }
    val date = instant.atZone(zone).toLocalDate()
    return "$host?latitude=$latitude&longitude=$longitude" +
        "&hourly=temperature_2m,relative_humidity_2m" +
        "&start_date=$date&end_date=$date&timezone=auto"
}

fun parseHourly(
    json: String,
    targetEpochSecond: Long,
): WeatherReading? =
    runCatching {
        val root = Json.parseToJsonElement(json).jsonObject
        val utcOffset = root["utc_offset_seconds"]?.jsonPrimitive?.intOrNull ?: 0
        val hourly = root["hourly"]?.jsonObject ?: return null
        val times = hourly["time"]?.jsonArray ?: return null
        val temperatures = hourly["temperature_2m"]?.jsonArray ?: return null
        val humidities = hourly["relative_humidity_2m"]?.jsonArray

        var bestIndex = -1
        var bestDiff = Long.MAX_VALUE
        times.forEachIndexed { index, element ->
            val epoch =
                runCatching {
                    LocalDateTime.parse(element.jsonPrimitive.content)
                        .toEpochSecond(ZoneOffset.ofTotalSeconds(utcOffset))
                }.getOrNull() ?: return@forEachIndexed
            val diff = abs(epoch - targetEpochSecond)
            if (diff < bestDiff) {
                bestDiff = diff
                bestIndex = index
            }
        }
        if (bestIndex < 0) return null

        val temp = temperatures.getOrNull(bestIndex)?.jsonPrimitive?.doubleOrNull ?: return null
        val humidity = humidities?.getOrNull(bestIndex)?.jsonPrimitive?.doubleOrNull?.roundToInt()
        WeatherReading(tempC = temp, humidityPct = humidity)
    }.getOrNull()
