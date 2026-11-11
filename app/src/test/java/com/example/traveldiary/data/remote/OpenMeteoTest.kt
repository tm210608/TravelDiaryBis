package com.example.traveldiary.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class OpenMeteoTest {
    private val now: Instant = Instant.parse("2026-11-02T12:00:00Z")

    @Test
    fun `openMeteoUrl uses forecast for recent dates`() {
        val url = openMeteoUrl(40.4, -3.7, now.minusSeconds(10 * 86400L), now, ZoneOffset.UTC)
        assertTrue(url.startsWith("https://api.open-meteo.com/v1/forecast"))
        assertTrue(url.contains("latitude=40.4"))
        assertTrue(url.contains("longitude=-3.7"))
        assertTrue(url.contains("hourly=temperature_2m,relative_humidity_2m"))
        assertTrue(url.contains("timezone=auto"))
    }

    @Test
    fun `openMeteoUrl uses archive for dates older than 92 days`() {
        val url = openMeteoUrl(40.4, -3.7, now.minusSeconds(100 * 86400L), now, ZoneOffset.UTC)
        assertTrue(url.startsWith("https://archive-api.open-meteo.com/v1/archive"))
    }

    @Test
    fun `openMeteoUrl uses forecast at exactly 92 days`() {
        val url = openMeteoUrl(40.4, -3.7, now.minusSeconds(92 * 86400L), now, ZoneOffset.UTC)
        assertTrue(url.startsWith("https://api.open-meteo.com/v1/forecast"))
    }

    @Test
    fun `openMeteoUrl formats the entry date in the given zone`() {
        val instant = Instant.parse("2024-05-01T23:30:00Z")
        val url = openMeteoUrl(35.0, 135.0, instant, now, ZoneOffset.UTC)
        assertTrue(url.contains("start_date=2024-05-01&end_date=2024-05-01"))
    }

    @Test
    fun `parseHourly returns nearest hour with utc offset applied`() {
        val json =
            """
            {
              "utc_offset_seconds": 3600,
              "hourly": {
                "time": ["2024-05-01T13:00", "2024-05-01T14:00", "2024-05-01T15:00"],
                "temperature_2m": [10.0, 16.4, 18.0],
                "relative_humidity_2m": [50, 45, 40]
              }
            }
            """.trimIndent()
        val target = Instant.parse("2024-05-01T13:10:00Z").epochSecond

        val reading = parseHourly(json, target)!!

        assertEquals(16.4, reading.tempC, 0.001)
        assertEquals(45, reading.humidityPct)
    }

    @Test
    fun `parseHourly tolerates missing humidity`() {
        val json =
            """
            {
              "utc_offset_seconds": 0,
              "hourly": {
                "time": ["2024-05-01T14:00"],
                "temperature_2m": [16.4]
              }
            }
            """.trimIndent()
        val target = Instant.parse("2024-05-01T14:00:00Z").epochSecond

        val reading = parseHourly(json, target)!!

        assertEquals(16.4, reading.tempC, 0.001)
        assertNull(reading.humidityPct)
    }

    @Test
    fun `parseHourly returns null on malformed json`() {
        assertNull(parseHourly("not json", 0L))
        assertNull(parseHourly("{}", 0L))
        assertNull(parseHourly("""{"hourly":{"time":[],"temperature_2m":[]}}""", 0L))
    }

    @Test
    fun `parseHourly returns null when temperatures are missing`() {
        val json =
            """
            {"hourly": {"time": ["2024-05-01T14:00"]}}
            """.trimIndent()
        assertNull(parseHourly(json, Instant.parse("2024-05-01T14:00:00Z").epochSecond))
    }
}
