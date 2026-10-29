package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.HomeFilter
import com.example.traveldiary.domain.model.TravelEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterEntriesUseCaseTest {

    private val useCase = FilterEntriesUseCase()

    private fun entry(id: Int, title: String, country: String, favourite: Boolean = false) =
        TravelEntry(id = id, title = title, location = "Loc$id", country = country, tag = "T", imageUrl = "url", isFavourite = favourite)

    private val paris = entry(1, "Paris", "Francia")
    private val kyoto = entry(2, "Kyoto", "Japón", favourite = true)
    private val cancun = entry(3, "Cancún", "México")
    private val mars = entry(4, "Base", "Marte", favourite = true)
    private val sydney = entry(5, "Sydney", "Australia")
    private val rome = entry(6, "Roma", "Italia")
    private val all = listOf(paris, kyoto, cancun, mars, sydney, rome)

    private fun ids(filter: HomeFilter, query: String = "") = useCase(all, filter, query).map { it.id }

    @Test
    fun `TODO returns every entry`() {
        assertEquals(listOf(1, 2, 3, 4, 5, 6), ids(HomeFilter.TODO))
    }

    @Test
    fun `continent chips return only entries of that continent`() {
        assertEquals(listOf(1, 6), ids(HomeFilter.EUROPA))
        assertEquals(listOf(2), ids(HomeFilter.ASIA))
        assertEquals(listOf(3), ids(HomeFilter.AMERICA))
    }

    @Test
    fun `FAVORITOS returns favourites from any continent`() {
        assertEquals(listOf(2, 4), ids(HomeFilter.FAVORITOS))
    }

    @Test
    fun `chip and query are combined with AND`() {
        assertEquals(listOf(6), ids(HomeFilter.EUROPA, "roma"))
        assertEquals(emptyList<Int>(), ids(HomeFilter.ASIA, "roma"))
        assertEquals(listOf(1), ids(HomeFilter.TODO, "franc"))
    }

    @Test
    fun `original order is preserved`() {
        val reversed = all.reversed()
        assertEquals(listOf(6, 1), useCase(reversed, HomeFilter.EUROPA, "").map { it.id })
    }
}
