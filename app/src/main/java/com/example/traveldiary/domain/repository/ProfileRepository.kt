package com.example.traveldiary.domain.repository

import com.example.traveldiary.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileStream(): Flow<UserProfile>

    suspend fun updateProfile(profile: UserProfile)
}
