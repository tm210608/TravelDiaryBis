package com.example.traveldiary.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEntry : Screen("add_entry")
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(entryId: Int) = "detail/$entryId"
    }
    object Camera : Screen("camera")
}
