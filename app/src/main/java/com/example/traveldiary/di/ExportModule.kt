package com.example.traveldiary.di

import com.example.traveldiary.data.export.ContentResolverPhotoLoader
import com.example.traveldiary.data.export.DiaryExporterImpl
import com.example.traveldiary.data.export.DiaryImporterImpl
import com.example.traveldiary.data.export.ExportStorageImpl
import com.example.traveldiary.data.export.ImportStorageImpl
import com.example.traveldiary.domain.export.DiaryExporter
import com.example.traveldiary.domain.export.DiaryImporter
import com.example.traveldiary.domain.export.ExportStorage
import com.example.traveldiary.domain.export.ImportStorage
import com.example.traveldiary.domain.export.PhotoLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExportModule {

    @Provides
    @Singleton
    fun provideDiaryExporter(): DiaryExporter = DiaryExporterImpl()

    @Provides
    @Singleton
    fun providePhotoLoader(loader: ContentResolverPhotoLoader): PhotoLoader = loader

    @Provides
    @Singleton
    fun provideExportStorage(storage: ExportStorageImpl): ExportStorage = storage

    @Provides
    @Singleton
    fun provideDiaryImporter(): DiaryImporter = DiaryImporterImpl()

    @Provides
    @Singleton
    fun provideImportStorage(storage: ImportStorageImpl): ImportStorage = storage
}
