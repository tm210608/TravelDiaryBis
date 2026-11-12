package com.example.traveldiary.domain.usecase

import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(profile: UserProfile) = repository.updateProfile(profile)
    }
