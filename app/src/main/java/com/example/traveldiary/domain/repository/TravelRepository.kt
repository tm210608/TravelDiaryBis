package com.example.traveldiary.domain.repository

import com.example.traveldiary.domain.model.TravelEntry
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level repository interface.
 */
interface TravelRepository {
    fun getAllEntriesStream(): Flow<List<TravelEntry>>
    fun getFavouriteEntriesStream(): Flow<List<TravelEntry>>
    suspend fun getEntryStream(id: Int): TravelEntry?
    suspend fun insertEntry(entry: TravelEntry)
    suspend fun deleteEntry(entry: TravelEntry)
    suspend fun updateEntry(entry: TravelEntry)
}
