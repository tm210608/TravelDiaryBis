package com.example.traveldiary

import android.content.Context
import com.example.traveldiary.data.OfflineTravelRepository
import com.example.traveldiary.data.TravelDatabase
import com.example.traveldiary.domain.repository.TravelRepository

interface AppContainer {
    val repository: TravelRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val repository: TravelRepository by lazy {
        OfflineTravelRepository(TravelDatabase.getDatabase(context).travelDao())
    }
}
