package com.example.traveldiary.di

import android.content.Context
import com.example.traveldiary.data.local.TravelDao
import com.example.traveldiary.data.local.TravelDatabase
import com.example.traveldiary.data.repository.TravelRepositoryImpl
import com.example.traveldiary.domain.repository.TravelRepository
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
    fun provideDatabase(@ApplicationContext context: Context): TravelDatabase =
        TravelDatabase.getDatabase(context)

    @Provides
    fun provideTravelDao(database: TravelDatabase): TravelDao = database.travelDao()

    @Provides
    @Singleton
    fun provideTravelRepository(dao: TravelDao): TravelRepository = TravelRepositoryImpl(dao)
}
