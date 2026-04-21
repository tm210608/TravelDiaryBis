package com.example.traveldiary.data

import androidx.room.*
import com.example.traveldiary.data.entity.TravelEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {
    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<TravelEntryEntity>>

    @Query("SELECT * FROM travel_entries WHERE isFavourite = 1")
    fun getFavouriteEntries(): Flow<List<TravelEntryEntity>>

    @Query("SELECT * FROM travel_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): TravelEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TravelEntryEntity)

    @Update
    suspend fun updateEntry(entry: TravelEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: TravelEntryEntity)
}
