package com.example.traveldiary.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home") {
        const val ROUTE_PATTERN = "home?export={export}"
        fun exportRoute() = "home?export=true"
    }
    object AddEntry : Screen("add_entry")
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(entryId: Int) = "detail/$entryId"
    }
    object EditEntry : Screen("edit_entry/{entryId}") {
        fun createRoute(entryId: Int) = "edit_entry/$entryId"
    }
    object Camera : Screen("camera")
    object Map : Screen("map")
    object Memories : Screen("memories")
    object Profile : Screen("profile")
}
