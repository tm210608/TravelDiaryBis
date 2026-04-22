package com.example.traveldiary.domain.model

import java.time.Instant

val featuredEntries = listOf(
    TravelEntry(
        id = 1,
        title = "Amanecer en las Maldivas",
        location = "Maafushi",
        country = "Maldivas",
        date = Instant.now(),
        tag = "Playa",
        imageUrl = "https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=800",
        description = "Un amanecer inolvidable rodeado de aguas cristalinas.",
        latitude = 3.9446,
        longitude = 73.4890
    ),
    TravelEntry(
        id = 2,
        title = "Luces de Tokio",
        location = "Shibuya",
        country = "Japón",
        date = Instant.now(),
        tag = "Ciudad",
        imageUrl = "https://images.unsplash.com/photo-1540959733332-e9ab42da39a6?w=800",
        description = "La energía vibrante del cruce de Shibuya de noche.",
        latitude = 35.6618,
        longitude = 139.7041
    )
)

val allEntries = listOf(
    TravelEntry(
        id = 3,
        title = "Alpes Suizos",
        location = "Zermatt",
        country = "Suiza",
        date = Instant.now(),
        tag = "Montaña",
        imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
        description = "Senderismo con vistas al Matterhorn.",
        latitude = 46.0207,
        longitude = 7.7491
    ),
    TravelEntry(
        id = 4,
        title = "Coliseo Romano",
        location = "Roma",
        country = "Italia",
        date = Instant.now(),
        tag = "Historia",
        imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=800",
        description = "Sintiendo la historia en el corazón de Roma.",
        latitude = 41.8902,
        longitude = 12.4922
    ),
    TravelEntry(
        id = 5,
        title = "Safari en Serengeti",
        location = "Mara",
        country = "Tanzania",
        date = Instant.now(),
        tag = "Naturaleza",
        imageUrl = "https://images.unsplash.com/photo-1516422217105-273727ca116e?w=800",
        description = "Avistamiento de los cinco grandes al atardecer.",
        latitude = -2.3333,
        longitude = 34.8333
    )
)
