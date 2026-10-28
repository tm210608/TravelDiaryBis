package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CountryCodeTest {

    @Test
    fun `countryCodeOf maps common countries in spanish and english`() {
        assertEquals("ESP", countryCodeOf("España"))
        assertEquals("ESP", countryCodeOf("Spain"))
        assertEquals("JPN", countryCodeOf("Japón"))
        assertEquals("JPN", countryCodeOf("Japan"))
        assertEquals("USA", countryCodeOf("Estados Unidos"))
        assertEquals("USA", countryCodeOf("United States"))
    }

    @Test
    fun `countryCodeOf is case and accent insensitive`() {
        assertEquals("MEX", countryCodeOf("México"))
        assertEquals("MEX", countryCodeOf("mexico"))
        assertEquals("FRA", countryCodeOf("  Francia  "))
    }

    @Test
    fun `countryCodeOf falls back to first three letters`() {
        assertEquals("ATL", countryCodeOf("Atlantis"))
        assertEquals("WAK", countryCodeOf("Wakanda"))
    }

    @Test
    fun `countryCodeOf returns null for blank countries`() {
        assertNull(countryCodeOf(""))
        assertNull(countryCodeOf("   "))
    }
}
