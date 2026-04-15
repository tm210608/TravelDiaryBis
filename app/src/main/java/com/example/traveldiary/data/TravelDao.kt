package com.example.traveldiary.data

import androidx.room.*
import com.example.traveldiary.model.TravelEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {
    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<TravelEntry>>

    @Query("SELECT * FROM travel_entries WHERE isFavourite = 1")
    fun getFavouriteEntries(): Flow<List<TravelEntry>>

    @Query("SELECT * FROM travel_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): TravelEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TravelEntry)

    @Update
    suspend fun updateEntry(entry: TravelEntry)

    @Delete
    suspend fun deleteEntry(entry: TravelEntry)
}
