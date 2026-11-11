package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class PdfCoverTest {
    @Test
    fun `buildPdfCover computes stats over given entries only`() {
        val entries =
            listOf(
                entry(daysAgo = 3, country = "España", lat = 40.4168, lon = -3.7038),
                entry(daysAgo = 2, country = "Japón", lat = 35.6762, lon = 139.6503),
                entry(daysAgo = 1, country = "España", lat = null, lon = null, favourite = true),
            )

        val cover = buildPdfCover(entries)

        assertEquals(3, cover.trips)
        assertEquals(1, cover.favourites)
        assertEquals(2, cover.countries)
        assertEquals(2, cover.continents)
        assertEquals(1, cover.legs)
        assertEquals(
            haversineKm(40.4168, -3.7038, 35.6762, 139.6503),
            cover.kmTotal,
            0.001,
        )
        assertEquals(3, cover.daysOnRoute)
    }

    @Test
    fun `buildPdfCover empty list gives zeros`() {
        val cover = buildPdfCover(emptyList())
        assertEquals(PdfCoverData(0, 0, 0, 0, 0.0, 0, 0), cover)
    }

    @Test
    fun `buildPdfCover ignores blank countries`() {
        val entries =
            listOf(
                entry(daysAgo = 1, country = "  "),
                entry(daysAgo = 2, country = "Perú"),
            )
        assertEquals(1, buildPdfCover(entries).countries)
    }

    private fun entry(
        daysAgo: Int = 0,
        country: String = "C",
        lat: Double? = null,
        lon: Double? = null,
        favourite: Boolean = false,
    ) = TravelEntry(
        id = 0,
        title = "T",
        location = "L",
        country = country,
        date = Instant.now().minusSeconds(daysAgo * 86400L),
        tag = "X",
        imageUrl = "",
        latitude = lat,
        longitude = lon,
        isFavourite = favourite,
    )
}
