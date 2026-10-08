package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContinentTest {

    @Test
    fun `continentOf maps european countries`() {
        assertEquals(Continent.EUROPE, continentOf("France"))
        assertEquals(Continent.EUROPE, continentOf("España"))
        assertEquals(Continent.EUROPE, continentOf("Greece"))
        assertEquals(Continent.EUROPE, continentOf("Alemania"))
    }

    @Test
    fun `continentOf maps asian countries`() {
        assertEquals(Continent.ASIA, continentOf("Japan"))
        assertEquals(Continent.ASIA, continentOf("Japón"))
        assertEquals(Continent.ASIA, continentOf("China"))
    }

    @Test
    fun `continentOf maps american countries`() {
        assertEquals(Continent.AMERICA, continentOf("Brazil"))
        assertEquals(Continent.AMERICA, continentOf("México"))
        assertEquals(Continent.AMERICA, continentOf("Estados Unidos"))
    }

    @Test
    fun `continentOf is case insensitive`() {
        assertEquals(Continent.EUROPE, continentOf("france"))
        assertEquals(Continent.EUROPE, continentOf("FRANCE"))
    }

    @Test
    fun `continentOf is accent insensitive`() {
        assertEquals(Continent.ASIA, continentOf("Japon"))
        assertEquals(Continent.AMERICA, continentOf("Mexico"))
        assertEquals(Continent.EUROPE, continentOf("Espana"))
    }

    @Test
    fun `continentOf ignores surrounding whitespace`() {
        assertEquals(Continent.EUROPE, continentOf("  France  "))
    }

    @Test
    fun `continentOf returns null for unknown or blank countries`() {
        assertNull(continentOf(""))
        assertNull(continentOf("   "))
        assertNull(continentOf("Atlantis"))
    }

    @Test
    fun `continentOf maps african and oceanian countries`() {
        assertEquals(Continent.AFRICA, continentOf("Marruecos"))
        assertEquals(Continent.AFRICA, continentOf("Egypt"))
        assertEquals(Continent.OCEANIA, continentOf("Australia"))
        assertEquals(Continent.OCEANIA, continentOf("New Zealand"))
    }
}
