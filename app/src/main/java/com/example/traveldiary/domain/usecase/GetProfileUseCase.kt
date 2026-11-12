package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        operator fun invoke(): Flow<UserProfile> = repository.getProfileStream()
    }
