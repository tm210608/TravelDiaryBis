package com.example.traveldiary.domain.model

data class UserProfile(
    val name: String = "Viajero",
    val photoUri: String? = null,
) {
    val initials: String
        get() =
            name
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
                .ifBlank { "V" }
}
