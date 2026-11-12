package com.example.traveldiary.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.traveldiary.domain.model.UserProfile
import com.example.traveldiary.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.profileDataStore by preferencesDataStore(name = "user_profile")

private val KEY_NAME = stringPreferencesKey("profile_name")
private val KEY_PHOTO_URI = stringPreferencesKey("profile_photo_uri")

@Singleton
class ProfileRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ProfileRepository {
        override fun getProfileStream(): Flow<UserProfile> =
            context.profileDataStore.data.map { prefs ->
                UserProfile(
                    name = prefs[KEY_NAME] ?: "Viajero",
                    photoUri = prefs[KEY_PHOTO_URI],
                )
            }

        override suspend fun updateProfile(profile: UserProfile) {
            context.profileDataStore.edit { prefs ->
                prefs[KEY_NAME] = profile.name
                if (profile.photoUri != null) {
                    prefs[KEY_PHOTO_URI] = profile.photoUri
                } else {
                    prefs.remove(KEY_PHOTO_URI)
                }
            }
        }
    }
