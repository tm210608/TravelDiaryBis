package com.example.traveldiary.domain.repository

import com.example.traveldiary.domain.model.WeatherReading
import java.time.Instant

interface WeatherRepository {
    suspend fun weatherAt(latitude: Double, longitude: Double, instant: Instant): WeatherReading?
}
