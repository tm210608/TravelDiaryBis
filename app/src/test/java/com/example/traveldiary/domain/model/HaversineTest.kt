package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class HaversineTest {

    @Test
    fun `haversineKm madrid to barcelona is about 505 km`() {
        val km = haversineKm(40.4168, -3.7038, 41.3851, 2.1734)
        assertEquals(505.0, km, 12.0)
    }

    @Test
    fun `haversineKm identical points is zero`() {
        assertEquals(0.0, haversineKm(41.3851, 2.1734, 41.3851, 2.1734), 0.001)
    }

    @Test
    fun `totalDistanceKm sums legs ordered by date`() {
        val madrid = entry(daysAgo = 3, lat = 40.4168, lon = -3.7038)
        val barcelona = entry(daysAgo = 2, lat = 41.3851, lon = 2.1734)
        val paris = entry(daysAgo = 1, lat = 48.8566, lon = 2.3522)

        val (km, legs) = totalDistanceKm(listOf(paris, madrid, barcelona))

        assertEquals(2, legs)
        assertEquals(haversineKm(40.4168, -3.7038, 41.3851, 2.1734) +
            haversineKm(41.3851, 2.1734, 48.8566, 2.3522), km, 0.001)
    }

    @Test
    fun `totalDistanceKm ignores entries without gps`() {
        val madrid = entry(daysAgo = 3, lat = 40.4168, lon = -3.7038)
        val sinGps = entry(daysAgo = 2, lat = null, lon = null)
        val barcelona = entry(daysAgo = 1, lat = 41.3851, lon = 2.1734)

        val (km, legs) = totalDistanceKm(listOf(madrid, sinGps, barcelona))

        assertEquals(1, legs)
        assertEquals(haversineKm(40.4168, -3.7038, 41.3851, 2.1734), km, 0.001)
    }

    @Test
    fun `totalDistanceKm empty or single gps entry gives zero`() {
        assertEquals(0.0 to 0, totalDistanceKm(emptyList()))
        assertEquals(0.0 to 0, totalDistanceKm(listOf(entry(1, lat = 41.0, lon = 2.0))))
    }

    @Test
    fun `formatKm uses spanish thousands separator`() {
        assertEquals("0", formatKm(0.0))
        assertEquals("505", formatKm(505.2))
        assertEquals("1.234", formatKm(1234.0))
        assertEquals("12.345", formatKm(12345.4))
        assertEquals("12.346", formatKm(12345.6))
    }

    private fun entry(daysAgo: Int = 0, lat: Double?, lon: Double?) = TravelEntry(
        id = 0,
        title = "T",
        location = "L",
        country = "C",
        date = Instant.now().minusSeconds(daysAgo * 86400L),
        tag = "X",
        imageUrl = "url",
        latitude = lat,
        longitude = lon,
    )
}
