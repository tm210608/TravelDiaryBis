package com.example.traveldiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.traveldiary.ui.screens.AddTravelEntryScreen
import com.example.traveldiary.ui.screens.CameraScreen
import com.example.traveldiary.ui.screens.EditTravelEntryScreen
import com.example.traveldiary.ui.screens.MapScreen
import com.example.traveldiary.ui.screens.MemoriesScreen
import com.example.traveldiary.ui.screens.ProfileScreen
import com.example.traveldiary.ui.screens.TravelDetailScreen
import com.example.traveldiary.ui.screens.TravelDiaryHomeScreen
import com.example.traveldiary.ui.viewmodel.AddEntryViewModel
import com.example.traveldiary.ui.viewmodel.EditEntryViewModel

private fun NavController.navigateTab(route: Any) =
    navigate(route) {
        launchSingleTop = true
        popUpTo<Home> { saveState = true }
    }

@Composable
fun TravelDiaryNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Splash,
    ) {
        val onTabNavigate: (Int) -> Unit = { index ->
            when (index) {
                0 -> navController.popBackStack<Home>(inclusive = false)
                1 -> navController.navigateTab(Map)
                2 -> navController.navigateTab(Memories)
                else -> navController.navigateTab(Profile)
            }
        }

        composable<Splash> {
            com.example.traveldiary.ui.screens.SplashScreen(
                onFinished = {
                    navController.navigate(Home()) {
                        popUpTo<Splash> { inclusive = true }
                    }
                },
            )
        }

        composable<Home> { backStackEntry ->
            val home = backStackEntry.toRoute<Home>()
            TravelDiaryHomeScreen(
                exportRequested = home.export,
                onEntryClick = { entry ->
                    navController.navigate(Detail(entry.id))
                },
                onAddClick = {
                    navController.navigate(AddEntry)
                },
                onMapClick = { onTabNavigate(1) },
                onMemoriesClick = { onTabNavigate(2) },
                onProfileClick = { onTabNavigate(3) },
                onAvatarClick = { navController.navigate(EditProfile) },
            )
        }

        composable<EditProfile> {
            com.example.traveldiary.ui.screens.EditProfileScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<AddEntry> { backStackEntry ->
            val result = backStackEntry.savedStateHandle.get<String>("capturedImageUri")
            val viewModel: AddEntryViewModel = hiltViewModel()

            LaunchedEffect(result) {
                result?.let { viewModel.updateImageUrl(it) }
            }

            AddTravelEntryScreen(
                navigateBack = { navController.popBackStack() },
                onCameraClick = { navController.navigate(Camera) },
                viewModel = viewModel,
            )
        }

        composable<Detail> { backStackEntry ->
            val entryId = backStackEntry.toRoute<Detail>().entryId
            val refreshVersion by backStackEntry.savedStateHandle
                .getStateFlow("refreshVersion", 0)
                .collectAsState()
            TravelDetailScreen(
                entryId = entryId,
                refreshKey = refreshVersion,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(EditEntry(entryId)) },
            )
        }

        composable<EditEntry> { backStackEntry ->
            val entryId = backStackEntry.toRoute<EditEntry>().entryId
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
                onEntryNotFound = { navController.popBackStack<Home>(inclusive = false) },
                onCameraClick = { navController.navigate(Camera) },
                viewModel = viewModel,
            )
        }

        composable<Camera> {
            CameraScreen(
                onImageCaptured = { uri ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("capturedImageUri", uri.toString())
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() },
            )
        }

        composable<Map> {
            MapScreen(
                onEntryClick = { entryId ->
                    navController.navigate(Detail(entryId))
                },
                onTabSelected = onTabNavigate,
                onAddClick = { navController.navigate(AddEntry) },
                showBottomBar = true,
            )
        }

        composable<Memories> {
            MemoriesScreen(
                onEntryClick = { entry ->
                    navController.navigate(Detail(entry.id))
                },
                onTabSelected = onTabNavigate,
                onAddClick = { navController.navigate(AddEntry) },
            )
        }

        composable<Profile> {
            ProfileScreen(
                onTabSelected = onTabNavigate,
                onEditProfileClick = { navController.navigate(EditProfile) },
                onExportClick = {
                    navController.navigate(Home(export = true)) {
                        popUpTo<Home> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
