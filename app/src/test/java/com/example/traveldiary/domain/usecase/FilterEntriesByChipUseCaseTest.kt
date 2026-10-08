package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.TravelEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterEntriesByChipUseCaseTest {

    private val useCase = FilterEntriesByChipUseCase()

    private fun entry(id: Int, country: String, isFavourite: Boolean = false) =
        TravelEntry(id = id, title = "T$id", location = "L", country = country,
            tag = "X", imageUrl = "url", isFavourite = isFavourite)

    private val entries = listOf(
        entry(1, "France", isFavourite = true),
        entry(2, "Japan"),
        entry(3, "Brazil"),
        entry(4, "Atlantis", isFavourite = true),
        entry(5, ""),
    )

    @Test
    fun `Todo returns all entries`() {
        assertEquals(5, useCase(entries, "Todo").size)
    }

    @Test
    fun `Europa returns only european countries`() {
        val result = useCase(entries, "Europa")
        assertEquals(listOf(1), result.map { it.id })
    }

    @Test
    fun `Asia returns only asian countries`() {
        val result = useCase(entries, "Asia")
        assertEquals(listOf(2), result.map { it.id })
    }

    @Test
    fun `America returns only american countries`() {
        val result = useCase(entries, "América")
        assertEquals(listOf(3), result.map { it.id })
    }

    @Test
    fun `Favoritos returns only favourite entries`() {
        val result = useCase(entries, "Favoritos")
        assertEquals(listOf(1, 4), result.map { it.id })
    }

    @Test
    fun `unknown or blank countries only appear in Todo or Favoritos`() {
        assertEquals(emptyList<Int>(), useCase(entries, "Europa").filter { it.country.isBlank() || it.country == "Atlantis" }.map { it.id })
    }

    @Test
    fun `unknown chip returns all entries`() {
        assertEquals(5, useCase(entries, "Inventado").size)
    }

    @Test
    fun `empty list returns empty`() {
        assertEquals(emptyList<TravelEntry>(), useCase(emptyList(), "Europa"))
    }
}
