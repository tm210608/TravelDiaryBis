package com.example.traveldiary.domain.model

import kotlin.math.roundToInt

data class WeatherReading(
    val tempC: Double,
    val humidityPct: Int?,
)

fun WeatherReading.formatWeatherTelemetry(): String {
    val temp = "${tempC.roundToInt()}°C"
    return humidityPct?.let { "· $temp · $it%" } ?: "· $temp"
}
