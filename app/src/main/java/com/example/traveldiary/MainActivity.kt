package com.example.traveldiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.traveldiary.ui.theme.TravelDiaryTheme
import com.example.traveldiary.navigation.TravelDiaryNavGraph
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            TravelDiaryTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    TravelDiaryNavGraph(navController = navController)
                }
            }
        }
    }
}
