package com.example.traveldiary.di

import android.content.Context
import androidx.room.Room
import com.example.traveldiary.data.local.TravelDao
import com.example.traveldiary.data.local.TravelDatabase
import com.example.traveldiary.data.repository.TravelRepositoryImpl
import com.example.traveldiary.domain.repository.TravelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TravelDatabase = Room.inMemoryDatabaseBuilder(context, TravelDatabase::class.java).build()

    @Provides
    fun provideTravelDao(database: TravelDatabase): TravelDao = database.travelDao()

    @Provides
    @Singleton
    fun provideTravelRepository(dao: TravelDao): TravelRepository = TravelRepositoryImpl(dao)
}
