package com.example.traveldiary.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Splash

@Serializable
data class Home(val export: Boolean = false)

@Serializable
data object AddEntry

@Serializable
data class Detail(val entryId: Int)

@Serializable
data class EditEntry(val entryId: Int)

@Serializable
data object Camera

@Serializable
data object Map

@Serializable
data object Memories

@Serializable
data object Profile

@Serializable
data object EditProfile
