package com.example.traveldiary.data

import com.example.traveldiary.data.entity.TravelEntryEntity
import com.example.traveldiary.data.mapper.toDomain
import com.example.traveldiary.data.mapper.toEntity
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la interfaz [TravelRepository] que utiliza Room como fuente de datos.
 */
@Singleton
class OfflineTravelRepository @Inject constructor(private val travelDao: TravelDao) : TravelRepository {
    
    override fun getAllEntriesStream(): Flow<List<TravelEntry>> = 
        travelDao.getAllEntries().map { entities -> entities.map { it.toDomain() } }
    
    override fun getFavouriteEntriesStream(): Flow<List<TravelEntry>> = 
        travelDao.getFavouriteEntries().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getEntryStream(id: Int): TravelEntry? = 
        travelDao.getEntryById(id)?.toDomain()

    override suspend fun insertEntry(entry: TravelEntry) = 
        travelDao.insertEntry(entry.toEntity())

    override suspend fun deleteEntry(entry: TravelEntry) = 
        travelDao.deleteEntry(entry.toEntity())

    override suspend fun updateEntry(entry: TravelEntry) = 
        travelDao.updateEntry(entry.toEntity())
}
