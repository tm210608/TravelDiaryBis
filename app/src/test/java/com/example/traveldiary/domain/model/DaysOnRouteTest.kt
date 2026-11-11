package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class DaysOnRouteTest {
    private fun entry(instant: Instant) =
        TravelEntry(
            title = "T",
            location = "L",
            country = "C",
            date = instant,
            tag = "X",
            imageUrl = "url",
        )

    @Test
    fun `two entries same calendar day count as one`() {
        val entries =
            listOf(
                entry(Instant.parse("2026-10-01T08:00:00Z")),
                entry(Instant.parse("2026-10-01T22:00:00Z")),
            )
        assertEquals(1, daysWithEntries(entries, ZoneOffset.UTC))
    }

    @Test
    fun `distinct days count separately`() {
        val entries =
            listOf(
                entry(Instant.parse("2026-10-01T08:00:00Z")),
                entry(Instant.parse("2026-10-02T08:00:00Z")),
                entry(Instant.parse("2026-10-05T08:00:00Z")),
            )
        assertEquals(3, daysWithEntries(entries, ZoneOffset.UTC))
    }

    @Test
    fun `empty list is zero`() {
        assertEquals(0, daysWithEntries(emptyList(), ZoneOffset.UTC))
    }
}
