package com.example.traveldiary.data.repository

import com.example.traveldiary.data.remote.OpenMeteoClient
import com.example.traveldiary.domain.model.WeatherReading
import com.example.traveldiary.domain.repository.WeatherRepository
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val client: OpenMeteoClient,
) : WeatherRepository {

    private val cache = ConcurrentHashMap<String, WeatherReading>()

    override suspend fun weatherAt(
        latitude: Double,
        longitude: Double,
        instant: Instant,
    ): WeatherReading? {
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val key = String.format(Locale.US, "%.2f,%.2f,%s", latitude, longitude, date)
        cache[key]?.let { return it }
        val reading = runCatching { client.fetch(latitude, longitude, instant) }.getOrNull()
        if (reading != null) cache[key] = reading
        return reading
    }
}
