package com.example.traveldiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.traveldiary.ui.screens.AddTravelEntryScreen
import com.example.traveldiary.ui.screens.EditTravelEntryScreen
import com.example.traveldiary.ui.screens.CameraScreen
import com.example.traveldiary.ui.screens.MapScreen
import com.example.traveldiary.ui.screens.MemoriesScreen
import com.example.traveldiary.ui.screens.ProfileScreen
import com.example.traveldiary.ui.screens.TravelDetailScreen
import com.example.traveldiary.ui.screens.TravelDiaryHomeScreen
import com.example.traveldiary.ui.viewmodel.AddEntryViewModel
import com.example.traveldiary.ui.viewmodel.EditEntryViewModel

@Composable
fun TravelDiaryNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        val onTabNavigate: (Int) -> Unit = { index ->
            when (index) {
                0 -> navController.popBackStack(Screen.Home.route, inclusive = false)
                else -> navController.navigate(
                    when (index) {
                        1 -> Screen.Map.route
                        2 -> Screen.Memories.route
                        else -> Screen.Profile.route
                    }
                ) {
                    launchSingleTop = true
                    popUpTo(Screen.Home.route) { saveState = true }
                }
            }
        }

        composable(
            route = Screen.Home.ROUTE_PATTERN,
            arguments = listOf(
                navArgument("export") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            TravelDiaryHomeScreen(
                exportRequested = backStackEntry.arguments?.getBoolean("export") ?: false,
                onEntryClick = { entry ->
                    navController.navigate(Screen.Detail.createRoute(entry.id))
                },
                onAddClick = {
                    navController.navigate(Screen.AddEntry.route)
                },
                onMapClick = { onTabNavigate(1) },
                onMemoriesClick = { onTabNavigate(2) },
                onProfileClick = { onTabNavigate(3) }
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
            val refreshVersion by backStackEntry.savedStateHandle
                .getStateFlow("refreshVersion", 0)
                .collectAsState()
            TravelDetailScreen(
                entryId = entryId,
                refreshKey = refreshVersion,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(Screen.EditEntry.createRoute(entryId)) },
            )
        }

        composable(
            route = Screen.EditEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: 0
            val capturedImageUri by backStackEntry.savedStateHandle
                .getStateFlow<String?>("capturedImageUri", null)
                .collectAsState()
            val viewModel: EditEntryViewModel = hiltViewModel()

            LaunchedEffect(capturedImageUri) {
                capturedImageUri?.let { uri ->
                    viewModel.updateImageUrl(uri)
                    backStackEntry.savedStateHandle.remove<String>("capturedImageUri")
                }
            }

            EditTravelEntryScreen(
                entryId = entryId,
                onBackClick = { navController.popBackStack() },
                onSaved = {
                    navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
                        val version = handle.get<Int>("refreshVersion") ?: 0
                        handle["refreshVersion"] = version + 1
                    }
                    navController.popBackStack()
                },
                onEntryNotFound = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                onCameraClick = { navController.navigate(Screen.Camera.route) },
                viewModel = viewModel,
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

        composable(Screen.Map.route) {
            MapScreen(
                onEntryClick = { entryId ->
                    navController.navigate(Screen.Detail.createRoute(entryId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Memories.route) {
            MemoriesScreen(
                onEntryClick = { entry ->
                    navController.navigate(Screen.Detail.createRoute(entry.id))
                },
                onTabSelected = onTabNavigate
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onTabSelected = onTabNavigate,
                onExportClick = {
                    navController.navigate(Screen.Home.exportRoute()) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
