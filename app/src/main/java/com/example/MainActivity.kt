package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ArtGeneratorTheme
import com.example.viewmodel.ArtViewModel
import com.example.viewmodel.ArtViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.ui.theme.ThemeManager.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            ArtGeneratorTheme {
                val navController = rememberNavController()
                val viewModel: ArtViewModel = viewModel(factory = ArtViewModelFactory(this))

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home_screen") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToHistory = { navController.navigate("history_screen") },
                                onNavigateToCommunity = { navController.navigate("community_screen") }
                            )
                        }
                        composable("history_screen") {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() },
                                onLoadToPrompt = { navController.popBackStack("home_screen", inclusive = false) }
                            )
                        }
                        composable("community_screen") {
                            CommunityScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() },
                                onLoadToPrompt = { navController.popBackStack("home_screen", inclusive = false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
