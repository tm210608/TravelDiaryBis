package com.example.traveldiary.model

val featuredEntries = listOf(
    TravelEntry(
        id = 1,
        title = "Eiffel Tower at Dusk",
        location = "Paris",
        country = "France",
        date = "Mar 14, 2025",
        tag = "City",
        description = "The iron lady standing tall against a pink and purple sky. We spent three hours just watching the lights flicker on, sipping on a cheap bottle of Bordeaux from a nearby café. Truly magical.",
        imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600",
        isFavourite = false,
    ),
    TravelEntry(
        id = 2,
        title = "Swiss Alps Adventure",
        location = "Zermatt",
        country = "Switzerland",
        date = "Jan 8, 2025",
        tag = "Mountains",
        description = "Waking up to the view of the Matterhorn is something I'll never forget. The air was crisp, almost sharp, and the silence of the snow-covered peaks was breathtaking. A hiker's paradise.",
        imageUrl = "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=600",
        isFavourite = true,
    ),
    TravelEntry(
        id = 3,
        title = "Kyoto Temple Grounds",
        location = "Kyoto",
        country = "Japan",
        date = "Nov 22, 2024",
        tag = "Culture",
        description = "The orange torii gates seemed to go on forever. Walking through Fushimi Inari at dawn allowed us to experience the spiritual side of Japan without the crowds. Every corner felt like a piece of history.",
        imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=600",
        isFavourite = false,
    ),
)

val allEntries = listOf(
    TravelEntry(
        id = 4,
        title = "Santorini Sunsets",
        location = "Oia",
        country = "Greece",
        date = "Feb 3, 2025",
        tag = "Islands",
        description = "White houses with blue domes clinging to the caldera cliffs. The sunset from Oia is famous for a reason—the whole world seems to turn gold for a few minutes.",
        imageUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=300",
    ),
    TravelEntry(
        id = 5,
        title = "Amazon Rainforest Trek",
        location = "Manaus",
        country = "Brazil",
        date = "Dec 17, 2024",
        tag = "Nature",
        description = "Deep in the lungs of the earth. We saw pink dolphins and heard the roar of howler monkeys every morning. Humid, challenging, but life-changing.",
        imageUrl = "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5?w=300",
    ),
)

val filterChips = listOf("Todo", "Europa", "Asia", "América", "Favoritos")
