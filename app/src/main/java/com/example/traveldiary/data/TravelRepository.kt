package com.example.traveldiary.data

import com.example.traveldiary.model.TravelEntry
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones de datos para los viajes.
 */
interface TravelRepository {
    fun getAllEntriesStream(): Flow<List<TravelEntry>>
    fun getFavouriteEntriesStream(): Flow<List<TravelEntry>>
    suspend fun getEntryStream(id: Int): TravelEntry?
    suspend fun insertEntry(entry: TravelEntry)
    suspend fun deleteEntry(entry: TravelEntry)
    suspend fun updateEntry(entry: TravelEntry)
}

/**
 * Implementación de la interfaz [TravelRepository] que utiliza Room como fuente de datos.
 */
class OfflineTravelRepository(private val travelDao: TravelDao) : TravelRepository {
    
    override fun getAllEntriesStream(): Flow<List<TravelEntry>> = travelDao.getAllEntries()
    
    override fun getFavouriteEntriesStream(): Flow<List<TravelEntry>> = travelDao.getFavouriteEntries()

    override suspend fun getEntryStream(id: Int): TravelEntry? = travelDao.getEntryById(id)

    override suspend fun insertEntry(entry: TravelEntry) = travelDao.insertEntry(entry)

    override suspend fun deleteEntry(entry: TravelEntry) = travelDao.deleteEntry(entry)

    override suspend fun updateEntry(entry: TravelEntry) = travelDao.updateEntry(entry)
}
