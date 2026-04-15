package com.example.traveldiary

import android.app.Application

class TravelDiaryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
