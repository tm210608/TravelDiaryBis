package com.example.traveldiary.domain.export

import com.example.traveldiary.domain.model.TravelEntry

sealed interface ImportResult {
    data class Success(val entries: List<TravelEntry>) : ImportResult
    data object InvalidFile : ImportResult
    data object UnsupportedVersion : ImportResult
    data object Empty : ImportResult
}
