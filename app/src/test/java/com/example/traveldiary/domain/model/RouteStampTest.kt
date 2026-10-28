package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RouteStampTest {

    private val day0 = Instant.parse("2026-01-01T12:00:00Z")
    private val day1 = Instant.parse("2026-02-01T12:00:00Z")
    private val day2 = Instant.parse("2026-03-01T12:00:00Z")

    @Test
    fun `buildRouteStamps dedupes countries and keeps earliest visit`() {
        val stamps = buildRouteStamps(
            listOf(
                entry("España", day2),
                entry("espana", day0),
                entry("Japan", day1),
            )
        )

        assertEquals(2, stamps.size)
        val esp = stamps.first { it.code == "ESP" }
        assertEquals(day0, esp.firstVisit)
        val jpn = stamps.first { it.code == "JPN" }
        assertEquals(day1, jpn.firstVisit)
    }

    @Test
    fun `buildRouteStamps sorts by first visit descending`() {
        val stamps = buildRouteStamps(
            listOf(
                entry("France", day1),
                entry("Japan", day2),
                entry("Spain", day0),
            )
        )

        assertEquals(listOf("JPN", "FRA", "ESP"), stamps.map { it.code })
    }

    @Test
    fun `buildRouteStamps caps at max stamps`() {
        val entries = (1..10).map { i -> entry("Country$i", day0.plusSeconds(i * 86400L)) }
        assertEquals(8, buildRouteStamps(entries).size)
    }

    @Test
    fun `buildRouteStamps ignores blank countries`() {
        val stamps = buildRouteStamps(
            listOf(entry("  ", day0), entry("", day1), entry("Spain", day2))
        )
        assertEquals(1, stamps.size)
        assertEquals("ESP", stamps.first().code)
    }

    @Test
    fun `buildRouteStamps rotation is deterministic and bounded`() {
        val entries = listOf(entry("Spain", day0), entry("Japan", day1), entry("Perú", day2))
        val first = buildRouteStamps(entries)
        val second = buildRouteStamps(entries)

        assertEquals(first.map { it.rotation }, second.map { it.rotation })
        first.forEach { assertTrue(it.rotation in -6f..6f) }
    }

    @Test
    fun `buildRouteStamps label is uppercased trimmed country`() {
        val stamps = buildRouteStamps(listOf(entry("  españa  ", day0)))
        assertEquals("ESPAÑA", stamps.first().label)
    }

    private fun entry(country: String, date: Instant) = TravelEntry(
        id = 0,
        title = "T",
        location = "L",
        country = country,
        date = date,
        tag = "X",
        imageUrl = "url",
    )
}
