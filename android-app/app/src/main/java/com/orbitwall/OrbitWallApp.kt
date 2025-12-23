package com.orbitwall

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orbitwall.model.PredefinedRegions
import com.orbitwall.ui.category.CategoryScreen
import com.orbitwall.ui.downloads.DownloadsScreen
import com.orbitwall.ui.downloads.SavedWallpaperEditorScreen
import com.orbitwall.ui.editor.EditorScreen
import com.orbitwall.ui.gallery.GalleryScreen
import com.orbitwall.ui.gallery.GalleryViewModel
import com.orbitwall.ui.home.HomeScreen
import com.orbitwall.ui.navigation.BottomNavigationBar
import com.orbitwall.ui.splash.SplashScreen

@Composable
fun OrbitWallApp() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    if (showSplash) {
        SplashScreen(
            onSplashComplete = {
                showSplash = false
            }
        )
    } else {
        Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = androidx.compose.ui.Modifier.weight(1f)
            ) {
                composable("home") {
                    val galleryViewModel: GalleryViewModel = viewModel()
                    HomeScreen(
                        onRegionSelected = { region ->
                            navController.navigate("editor/${region.id}")
                        },
                        galleryViewModel = galleryViewModel
                    )
                }
                
                composable("category") {
                    val galleryViewModel: GalleryViewModel = viewModel()
                    CategoryScreen(
                        onCategorySelected = { categoryId ->
                            navController.navigate("gallery/$categoryId")
                        },
                        galleryViewModel = galleryViewModel
                    )
                }
                
                composable("downloads") {
                    com.orbitwall.ui.downloads.DownloadsScreen(
                        onSavedWallpaperSelected = { savedWallpaper ->
                            // URL encode the URI string to handle special characters
                            val encodedUri = java.net.URLEncoder.encode(savedWallpaper.uri.toString(), "UTF-8")
                            navController.navigate("saved/$encodedUri")
                        }
                    )
                }
                
                composable("gallery") {
                    val galleryViewModel: GalleryViewModel = viewModel()
                    GalleryScreen(
                        onRegionSelected = { region ->
                            navController.navigate("editor/${region.id}")
                        },
                        onLogout = {},
                        galleryViewModel = galleryViewModel
                    )
                }
                
                composable(
                    route = "gallery/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                ) { entry ->
                    val categoryId = entry.arguments?.getString("categoryId") ?: "all"
                    val galleryViewModel: GalleryViewModel = viewModel()
                    GalleryScreen(
                        onRegionSelected = { region ->
                            navController.navigate("editor/${region.id}")
                        },
                        onLogout = {},
                        galleryViewModel = galleryViewModel,
                        initialCategoryId = categoryId
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
                
                composable(
                    route = "saved/{imageUri}",
                    arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
                ) { entry ->
                    val encodedUriString = entry.arguments?.getString("imageUri") ?: ""
                    // URL decode the URI string
                    val imageUriString = try {
                        java.net.URLDecoder.decode(encodedUriString, "UTF-8")
                    } catch (e: Exception) {
                        encodedUriString // Fallback to original if decoding fails
                    }
                    SavedWallpaperEditorScreen(
                        imageUriString = imageUriString,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            
            // Bottom Navigation Bar (only show on main screens)
            if (currentRoute in listOf("home", "category", "downloads")) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

