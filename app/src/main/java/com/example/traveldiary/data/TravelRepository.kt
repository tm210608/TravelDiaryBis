package com.example.traveldiary.data

import com.example.traveldiary.data.entity.TravelEntryEntity
import com.example.traveldiary.data.mapper.toDomain
import com.example.traveldiary.data.mapper.toEntity
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.example.traveldiary.data.util.ImageManager
import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la interfaz [TravelRepository] que utiliza Room como fuente de datos.
 */
@Singleton
class OfflineTravelRepository @Inject constructor(
    private val travelDao: TravelDao,
    private val imageManager: ImageManager
) : TravelRepository {
    
    override fun getAllEntriesStream(): Flow<List<TravelEntry>> = 
        travelDao.getAllEntries().map { entities -> 
            entities.map { it.toDomain().copy(imageUrl = imageManager.getImagePath(it.imageUrl)) } 
        }
    
    override fun getFavouriteEntriesStream(): Flow<List<TravelEntry>> = 
        travelDao.getFavouriteEntries().map { entities -> 
            entities.map { it.toDomain().copy(imageUrl = imageManager.getImagePath(it.imageUrl)) } 
        }

    override suspend fun getEntryStream(id: Int): TravelEntry? = 
        travelDao.getEntryById(id)?.let { 
            it.toDomain().copy(imageUrl = imageManager.getImagePath(it.imageUrl)) 
        }

    override suspend fun insertEntry(entry: TravelEntry) {
        val fileName = imageManager.saveImagePermanently(Uri.parse(entry.imageUrl)) ?: entry.imageUrl
        travelDao.insertEntry(entry.copy(imageUrl = fileName).toEntity())
    }

    override suspend fun deleteEntry(entry: TravelEntry) {
        // Antes de borrar de la DB, intentamos borrar el archivo físico
        imageManager.deleteImage(entry.imageUrl)
        travelDao.deleteEntry(entry.toEntity())
    }

    override suspend fun updateEntry(entry: TravelEntry) = 
        travelDao.updateEntry(entry.toEntity())
}
