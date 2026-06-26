package com.example.traveldiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.traveldiary.ui.screens.AddTravelEntryScreen
import com.example.traveldiary.ui.screens.CameraScreen
import com.example.traveldiary.ui.screens.TravelDetailScreen
import com.example.traveldiary.ui.screens.TravelDiaryHomeScreen
import com.example.traveldiary.ui.viewmodel.AddEntryViewModel

@Composable
fun TravelDiaryNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            TravelDiaryHomeScreen(
                onEntryClick = { entry ->
                    navController.navigate(Screen.Detail.createRoute(entry.id))
                },
                onAddClick = {
                    navController.navigate(Screen.AddEntry.route)
                }
            )
        }

        composable(Screen.AddEntry.route) { backStackEntry ->
            val result = backStackEntry.savedStateHandle.get<String>("capturedImageUri")
            val viewModel: AddEntryViewModel = hiltViewModel()

            LaunchedEffect(result) {
                result?.let { viewModel.updateImageUrl(it) }
            }

            AddTravelEntryScreen(
                navigateBack = { navController.popBackStack() },
                onCameraClick = { navController.navigate(Screen.Camera.route) },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: 0
            TravelDetailScreen(
                entryId = entryId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { uri ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("capturedImageUri", uri.toString())
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }
    }
}
