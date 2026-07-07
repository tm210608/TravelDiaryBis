package com.example.traveldiary.data.mapper

import com.example.traveldiary.data.local.TravelEntryEntity
import com.example.traveldiary.domain.model.TravelEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TravelEntryMapperTest {

    private val now = Instant.now()

    private val entity = TravelEntryEntity(
        id = 1,
        title = "Test Trip",
        location = "Barcelona",
        country = "Spain",
        date = now,
        tag = "City",
        imageUrl = "https://example.com/image.jpg",
        description = "Amazing trip",
        latitude = 41.3874,
        longitude = 2.1686,
        isFavourite = true,
    )

    @Test
    fun `entity to domain maps all fields correctly`() {
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.location, domain.location)
        assertEquals(entity.country, domain.country)
        assertEquals(entity.date, domain.date)
        assertEquals(entity.tag, domain.tag)
        assertEquals(entity.imageUrl, domain.imageUrl)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.latitude, domain.latitude)
        assertEquals(entity.longitude, domain.longitude)
        assertEquals(entity.isFavourite, domain.isFavourite)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain = entity.toDomain()
        val resultEntity = domain.toEntity()

        assertEquals(entity.id, resultEntity.id)
        assertEquals(entity.title, resultEntity.title)
        assertEquals(entity.location, resultEntity.location)
        assertEquals(entity.country, resultEntity.country)
        assertEquals(entity.date, resultEntity.date)
        assertEquals(entity.tag, resultEntity.tag)
        assertEquals(entity.imageUrl, resultEntity.imageUrl)
        assertEquals(entity.description, resultEntity.description)
        assertEquals(entity.latitude, resultEntity.latitude)
        assertEquals(entity.longitude, resultEntity.longitude)
        assertEquals(entity.isFavourite, resultEntity.isFavourite)
    }

    @Test
    fun `entity to domain to entity is identity`() {
        val result = entity.toDomain().toEntity()
        assertEquals(entity, result)
    }

    @Test
    fun `toDomainList maps all entities`() {
        val entities = listOf(entity, entity.copy(id = 2))
        val domains = entities.toDomainList()

        assertEquals(entities.size, domains.size)
        domains.forEachIndexed { index, domain ->
            assertEquals(entities[index], domain.toEntity())
        }
    }

    @Test
    fun `maps nullable fields correctly when null`() {
        val entityWithNulls = entity.copy(latitude = null, longitude = null)
        val domain = entityWithNulls.toDomain()

        assertEquals(null, domain.latitude)
        assertEquals(null, domain.longitude)
    }

    @Test
    fun `maps default values correctly`() {
        val defaultEntity = TravelEntryEntity(
            title = "Minimal",
            location = "X",
            country = "Y",
            tag = "Trip",
            imageUrl = "url",
        )
        val domain = defaultEntity.toDomain()

        assertEquals(0, domain.id)
        assertEquals(true, domain.date.epochSecond > 0)
        assertEquals(false, domain.isFavourite)
        assertEquals("", domain.description)
        assertEquals(null, domain.latitude)
        assertEquals(null, domain.longitude)
    }
}
