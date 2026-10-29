package com.example.traveldiary.domain

import com.example.traveldiary.domain.model.Continent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContinentResolverTest {

    @Test
    fun `resolve ignores case and surrounding spaces`() {
        listOf("France", "Francia", "francia ", "FRANCIA").forEach {
            assertEquals(it, Continent.EUROPA, ContinentResolver.resolve(it))
        }
    }

    @Test
    fun `resolve ignores accents`() {
        assertEquals(Continent.ASIA, ContinentResolver.resolve("Japón"))
        assertEquals(Continent.ASIA, ContinentResolver.resolve("Japon"))
        assertEquals(Continent.EUROPA, ContinentResolver.resolve("España"))
        assertEquals(Continent.EUROPA, ContinentResolver.resolve("Espana"))
    }

    @Test
    fun `resolve maps north central south america and caribbean to AMERICA`() {
        listOf("México", "Estados Unidos", "EEUU", "EE.UU.", "USA", "Brasil", "Cuba", "Canada", "Canadá").forEach {
            assertEquals(it, Continent.AMERICA, ContinentResolver.resolve(it))
        }
    }

    @Test
    fun `resolve maps africa and oceania`() {
        assertEquals(Continent.AFRICA, ContinentResolver.resolve("Marruecos"))
        assertEquals(Continent.OCEANIA, ContinentResolver.resolve("Australia"))
    }

    @Test
    fun `resolve returns null for unknown or blank country`() {
        assertNull(ContinentResolver.resolve("Marte"))
        assertNull(ContinentResolver.resolve("Fransia"))
        assertNull(ContinentResolver.resolve(""))
        assertNull(ContinentResolver.resolve("   "))
    }

    @Test
    fun `resolve follows UN M49 for transcontinental countries`() {
        assertEquals(Continent.EUROPA, ContinentResolver.resolve("Rusia"))
        assertEquals(Continent.ASIA, ContinentResolver.resolve("Turquía"))
        assertEquals(Continent.AFRICA, ContinentResolver.resolve("Egipto"))
    }
}
