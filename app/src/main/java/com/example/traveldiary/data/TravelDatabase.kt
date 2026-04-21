package com.example.traveldiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.traveldiary.data.entity.TravelEntryEntity

@Database(entities = [TravelEntryEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {
    
    abstract fun travelDao(): TravelDao

    companion object {
        @Volatile
        private var Instance: TravelDatabase? = null

        fun getDatabase(context: Context): TravelDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    TravelDatabase::class.java,
                    "travel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
