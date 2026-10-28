package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.PassportData
import com.example.traveldiary.domain.model.buildRouteStamps
import com.example.traveldiary.domain.model.totalDistanceKm
import com.example.traveldiary.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPassportUseCase @Inject constructor(
    private val repository: TravelRepository
) {
    operator fun invoke(): Flow<PassportData> =
        repository.getAllEntriesStream().map { entries ->
            val (km, legs) = totalDistanceKm(entries)
            PassportData(
                kmTotal = km,
                legs = legs,
                stamps = buildRouteStamps(entries),
            )
        }
}
