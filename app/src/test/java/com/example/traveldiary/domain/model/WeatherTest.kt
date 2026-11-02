package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherTest {

    @Test
    fun `formatWeatherTelemetry rounds temp and shows humidity`() {
        assertEquals("· 16°C · 45%", WeatherReading(16.4, 45).formatWeatherTelemetry())
        assertEquals("· 17°C · 45%", WeatherReading(16.6, 45).formatWeatherTelemetry())
    }

    @Test
    fun `formatWeatherTelemetry without humidity shows temp only`() {
        assertEquals("· 16°C", WeatherReading(16.4, null).formatWeatherTelemetry())
    }

    @Test
    fun `formatWeatherTelemetry handles negative temps`() {
        assertEquals("· -2°C · 90%", WeatherReading(-2.3, 90).formatWeatherTelemetry())
    }
}
