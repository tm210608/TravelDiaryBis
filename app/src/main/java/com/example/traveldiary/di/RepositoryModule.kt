package com.example.traveldiary.di

import com.example.traveldiary.data.OfflineTravelRepository
import com.example.traveldiary.domain.repository.TravelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTravelRepository(
        offlineTravelRepository: OfflineTravelRepository
    ): TravelRepository
}
