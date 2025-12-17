package com.orbitwall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orbitwall.model.PredefinedRegions
import com.orbitwall.ui.editor.EditorScreen
import com.orbitwall.ui.gallery.GalleryScreen
import com.orbitwall.ui.gallery.GalleryViewModel
// import com.orbitwall.ui.login.LoginScreen // Disabled - no login required

@Composable
fun OrbitWallApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gallery") {
        // Login screen disabled - going directly to gallery
        // composable("login") {
        //     LoginScreen(
        //         onLoginSuccess = {
        //             navController.navigate("gallery") {
        //                 popUpTo("login") { inclusive = true }
        //             }
        //         }
        //     )
        // }
        composable("gallery") {
            val galleryViewModel: GalleryViewModel = viewModel()
            GalleryScreen(
                onRegionSelected = { region ->
                    navController.navigate("editor/${region.id}")
                },
                onLogout = {
                    // Logout disabled - just keeping the app open
                    // navController.navigate("login") {
                    //     popUpTo("gallery") { inclusive = true }
                    // }
                },
                galleryViewModel = galleryViewModel
            )
        }
        composable(
            route = "editor/{regionId}",
            arguments = listOf(navArgument("regionId") { type = NavType.StringType })
        ) { entry ->
            val regionId = entry.arguments?.getString("regionId")
            val currentRegion = remember(regionId) {
                PredefinedRegions.firstOrNull { it.id == regionId }
                    ?: PredefinedRegions.first()
            }
            var region by remember(regionId) { mutableStateOf(currentRegion) }
            
            // Update region when route changes (e.g., from swipe navigation)
            androidx.compose.runtime.LaunchedEffect(regionId) {
                PredefinedRegions.firstOrNull { it.id == regionId }?.let {
                    region = it
                }
            }
            
            EditorScreen(
                region = region,
                allRegions = PredefinedRegions,
                onRegionChanged = { newRegion ->
                    // Navigate to new region
                    navController.navigate("editor/${newRegion.id}") {
                        // Replace current route instead of adding to stack
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

