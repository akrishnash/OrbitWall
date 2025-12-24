package com.orbitwall.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.orbitwall.model.Region
import com.orbitwall.model.RegionHelper
import com.orbitwall.ui.gallery.GalleryViewModel
import com.orbitwall.utils.PreferencesManager
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun HomeScreen(
    onRegionSelected: (Region) -> Unit,
    galleryViewModel: GalleryViewModel = viewModel()
) {
    val homepage by galleryViewModel.homepage.collectAsState(null)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    // Get all regions (shuffled for "Random" effect)
    val allRegions = remember(homepage) {
        homepage?.categories?.firstOrNull { it.id == "all" }?.regions?.shuffled()
            ?: emptyList()
    }
    
    // Calculate grid columns - 4 columns for larger screens, 2 for smaller
    val columns = when {
        screenWidth < 600 -> 2
        screenWidth < 960 -> 3
        else -> 4
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with title and dark mode toggle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OrbitWall",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    // Dark mode toggle
                    val context = LocalContext.current
                    val useSystemTheme = remember { PreferencesManager.useSystemTheme(context) }
                    val darkModeEnabled = remember { PreferencesManager.isDarkModeEnabled(context) }
                    val systemDarkTheme = isSystemInDarkTheme()
                    val isDarkTheme = if (useSystemTheme) systemDarkTheme else darkModeEnabled
                    
                    IconButton(
                        onClick = {
                            if (useSystemTheme) {
                                // Switch to manual mode
                                PreferencesManager.setUseSystemTheme(context, false)
                                PreferencesManager.setDarkMode(context, !systemDarkTheme)
                            } else {
                                // Toggle dark mode
                                PreferencesManager.setDarkMode(context, !darkModeEnabled)
                            }
                            // Restart activity to apply theme change
                            (context as? android.app.Activity)?.recreate()
                        }
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Light Mode" else "Dark Mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Scrollable grid of all images
            if (allRegions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = allRegions,
                        key = { it.id }
                    ) { region ->
                        FeaturedImageCard(
                            region = region,
                            onClick = { onRegionSelected(region) }
                        )
                    }
                }
            } else {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedImageCard(
    region: Region,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = try {
                region.heroImage ?: RegionHelper.getPreviewImageUrl(region)
            } catch (e: Exception) {
                null
            }
            
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                        .networkCachePolicy(coil.request.CachePolicy.ENABLED)
                        .allowHardware(true)
                        .crossfade(false)
                        .build(),
                    contentDescription = region.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = region.name,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            // Region name at bottom
            Text(
                text = region.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

