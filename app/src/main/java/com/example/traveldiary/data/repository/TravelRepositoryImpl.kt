package com.example.traveldiary.data.repository

import com.example.traveldiary.data.local.TravelDao
import com.example.traveldiary.data.mapper.toDomain
import com.example.traveldiary.data.mapper.toDomainList
import com.example.traveldiary.data.mapper.toEntity
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TravelRepositoryImpl(
    private val travelDao: TravelDao
) : TravelRepository {

    override fun getAllEntriesStream(): Flow<List<TravelEntry>> =
        travelDao.getAllEntries().map { it.toDomainList() }

    override fun getFavouriteEntriesStream(): Flow<List<TravelEntry>> =
        travelDao.getFavouriteEntries().map { it.toDomainList() }

    override suspend fun getEntryStream(id: Int): TravelEntry? =
        travelDao.getEntryById(id)?.toDomain()

    override suspend fun insertEntry(entry: TravelEntry) =
        travelDao.insertEntry(entry.toEntity())

    override suspend fun deleteEntry(entry: TravelEntry) =
        travelDao.deleteEntry(entry.toEntity())

    override suspend fun updateEntry(entry: TravelEntry) =
        travelDao.updateEntry(entry.toEntity())
}
