package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class GeoFormatTest {

    private val utc = ZoneId.of("UTC")

    @Test
    fun `formatToCardinal picks N for positive latitude`() {
        assertEquals("41.3850\u00b0N", formatToCardinal(41.385, 'N', 'S'))
    }

    @Test
    fun `formatToCardinal picks S for negative latitude`() {
        assertEquals("33.8688\u00b0S", formatToCardinal(-33.8688, 'N', 'S'))
    }

    @Test
    fun `formatToCardinal picks W for negative longitude`() {
        assertEquals("0.1276\u00b0W", formatToCardinal(-0.1276, 'E', 'W'))
    }

    @Test
    fun `formatToCardinal treats zero as positive cardinal`() {
        assertEquals("0.0000\u00b0N", formatToCardinal(0.0, 'N', 'S'))
        assertEquals("0.0000\u00b0E", formatToCardinal(-0.0, 'E', 'W'))
    }

    @Test
    fun `toCardinalLat and toCardinalLon use N-S and E-W`() {
        assertEquals("35.7681\u00b0N", 35.7681.toCardinalLat())
        assertEquals("2.1734\u00b0E", 2.1734.toCardinalLon())
        assertEquals("35.7681\u00b0S", (-35.7681).toCardinalLat())
    }

    @Test
    fun `hasGps requires both coordinates`() {
        assertTrue(entry(lat = 41.385, lon = 2.173).hasGps)
        assertFalse(entry(lat = null, lon = 2.173).hasGps)
        assertFalse(entry(lat = 41.385, lon = null).hasGps)
        assertFalse(entry(lat = null, lon = null).hasGps)
    }

    @Test
    fun `telemetryLine shows cardinal coordinates when gps present`() {
        assertEquals(
            "41.3850\u00b0N \u00b7 2.1734\u00b0E",
            entry(lat = 41.385, lon = 2.1734).telemetryLine(),
        )
    }

    @Test
    fun `telemetryLine falls back to place when gps missing`() {
        assertEquals("Barcelona, Espa\u00f1a", entry(lat = null, lon = null).telemetryLine())
        assertEquals("Barcelona, Espa\u00f1a", entry(lat = 41.385, lon = null).telemetryLine())
    }

    @Test
    fun `formatToTicket splits day month year and time`() {
        val instant = Instant.parse("2026-10-08T17:46:00Z")
        val ticket = instant.formatToTicket(utc)
        assertEquals("08", ticket.day)
        assertEquals("OCT", ticket.month)
        assertEquals("2026", ticket.year)
        assertEquals("26", ticket.yearShort)
        assertEquals("17:46", ticket.time)
    }

    @Test
    fun `formatToTicket zero pads single digit values`() {
        val instant = Instant.parse("2026-02-03T04:05:00Z")
        val ticket = instant.formatToTicket(utc)
        assertEquals("03", ticket.day)
        assertEquals("FEB", ticket.month)
        assertEquals("04:05", ticket.time)
    }

    @Test
    fun `formatToCompact renders dd MMM yy in spanish`() {
        assertEquals(
            "08\u00b7OCT\u00b726",
            Instant.parse("2026-10-08T17:46:00Z").formatToCompact(utc),
        )
        assertEquals(
            "31\u00b7DIC\u00b725",
            Instant.parse("2025-12-31T23:59:00Z").formatToCompact(utc),
        )
    }

    @Test
    fun `formatToTicket honours the given zone`() {
        val instant = Instant.parse("2026-10-08T23:30:00Z")
        val tokyo = ZoneId.of("Asia/Tokyo")
        assertEquals("09", instant.formatToTicket(tokyo).day)
        assertEquals("08", instant.formatToTicket(utc).day)
    }

    private fun entry(lat: Double?, lon: Double?) = TravelEntry(
        id = 1,
        title = "Prueba",
        location = "Barcelona",
        country = "Espa\u00f1a",
        tag = "Urbano",
        imageUrl = "file://x",
        latitude = lat,
        longitude = lon,
    )
}
