package com.example.traveldiary.di

import com.example.traveldiary.data.repository.WeatherRepositoryImpl
import com.example.traveldiary.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    @Provides
    @Singleton
    fun provideWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository = impl
}
