package com.example.traveldiary.di

import android.content.Context
import com.example.traveldiary.data.TravelDatabase
import com.example.traveldiary.data.TravelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TravelDatabase {
        return TravelDatabase.getDatabase(context)
    }

    @Provides
    fun provideTravelDao(database: TravelDatabase): TravelDao {
        return database.travelDao()
    }
}
