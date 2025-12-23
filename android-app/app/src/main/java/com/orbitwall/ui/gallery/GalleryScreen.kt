package com.orbitwall.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.orbitwall.model.Region
import com.orbitwall.model.RegionHelper
import com.orbitwall.ui.theme.Blue50
import com.orbitwall.ui.theme.Blue600
import com.orbitwall.ui.theme.Slate50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onRegionSelected: (Region) -> Unit,
    onLogout: () -> Unit,
    galleryViewModel: GalleryViewModel = viewModel(),
    initialCategoryId: String? = null
) {
    val homepage by galleryViewModel.homepage.collectAsState(null)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    // Filter state
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedCategoryId by remember(initialCategoryId) { 
        mutableStateOf<String?>(initialCategoryId ?: "all") 
    }
    
    // Update category when initialCategoryId changes
    androidx.compose.runtime.LaunchedEffect(initialCategoryId) {
        if (initialCategoryId != null) {
            selectedCategoryId = initialCategoryId
        }
    }
    
    // Calculate grid columns based on screen width (matching Figma: 1-4 columns)
    val columns = when {
        screenWidth < 600 -> 1  // sm: 1 column
        screenWidth < 960 -> 2  // md: 2 columns
        screenWidth < 1280 -> 3 // lg: 3 columns
        else -> 4                // xl: 4 columns
    }
    
    // Force load if still null after a delay
    LaunchedEffect(Unit) {
        if (homepage == null) {
            kotlinx.coroutines.delay(100)
        }
    }

    // Show loading or error state
    if (homepage == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Slate50, Blue50)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator()
                Text(
                    text = "Loading regions...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        return
    }

    // Get filtered regions based on selected category
    val filteredRegions = remember(selectedCategoryId, homepage) {
        homepage?.categories?.firstOrNull { it.id == (selectedCategoryId ?: "all") }?.regions
            ?: homepage?.categories?.firstOrNull { it.id == "all" }?.regions
            ?: homepage?.categories?.flatMap { it.regions }?.distinctBy { it.id } 
            ?: emptyList()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate50, Blue50)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header - matching Figma design
            val categoryName = remember(selectedCategoryId, homepage) {
                homepage?.categories?.firstOrNull { it.id == (selectedCategoryId ?: "all") }?.label
                    ?: "All"
            }
            Header(
                title = categoryName,
                wallpaperCount = filteredRegions.size,
                onFilterClick = { showFilterSheet = true }
            )

            // Grid layout - matching Figma design
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 88.dp // Extra bottom padding to avoid navigation buttons
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding() // Add padding for navigation bars
            ) {
                items(
                    items = filteredRegions,
                    key = { it.id }
                ) { region ->
                    GridWallpaperCard(region, onRegionSelected, columns)
                }
            }
        }
        
        // Filter Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState
            ) {
                FilterBottomSheet(
                    categories = homepage?.categories ?: emptyList(),
                    selectedCategoryId = selectedCategoryId ?: "all",
                    onCategorySelected = { categoryId ->
                        selectedCategoryId = categoryId
                        showFilterSheet = false
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    wallpaperCount: Int,
    onFilterClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(), // Add padding for status bar
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp) // More top padding, centered
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Logo + Title (centered better)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // Take available space to center better
                ) {
                    // Globe icon in blue box (matching Figma)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Blue600, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Globe icon - using a simple icon placeholder
                        // You can replace with a proper globe icon resource if available
                        Text(
                            text = "🌍",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$wallpaperCount wallpapers available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Right side: Filter button
                OutlinedButton(
                    onClick = onFilterClick,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Filters", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun GridWallpaperCard(
    region: Region,
    onClick: (Region) -> Unit,
    columns: Int = 2 // Pass columns from parent to calculate optimal image size
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f) // 3:4 aspect ratio matching Figma
            .clickable { onClick(region) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = try {
                region.heroImage ?: RegionHelper.getPreviewImageUrl(region)
            } catch (e: Exception) {
                null
            }
            
            if (imageUrl != null) {
                // Get display size for optimal image loading
                // Calculate approximate card size based on screen width and grid columns
                val displayMetrics = context.resources.displayMetrics
                val screenWidthPx = displayMetrics.widthPixels
                val screenWidthDp = screenWidthPx / displayMetrics.density
                val cardWidthDp = (screenWidthDp / columns) - 32 // Account for padding/spacing
                val cardWidthPx = (cardWidthDp * displayMetrics.density).toInt().coerceAtLeast(200)
                val cardHeightPx = (cardWidthPx * 4 / 3) // 3:4 aspect ratio
                
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .size(cardWidthPx, cardHeightPx) // Resize to exact card size - critical for performance
                        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                        .networkCachePolicy(coil.request.CachePolicy.ENABLED)
                        .allowHardware(true) // Use hardware bitmaps for better performance
                        .crossfade(false) // Disable crossfade for faster display
                        .build(),
                    contentDescription = region.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Top badge - always visible (matching Figma)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = region.name,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    },
                    colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        labelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
            
            // Gradient overlay at bottom for text readability (always visible)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            // Bottom info - always visible (matching Figma style)
            Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = region.tags.firstOrNull() ?: "Satellite",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                labelColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Text(
                            text = "${region.location.lat.toInt()}°, ${region.location.lon.toInt()}°",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    categories: List<com.orbitwall.model.GalleryCategory>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filter by Category",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Select a category to filter wallpapers",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(Modifier.height(8.dp))
        
        // Category chips
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(categories.size) { index ->
                val category = categories[index]
                val isSelected = category.id == selectedCategoryId
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(category.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${category.regions.size} wallpapers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onCategorySelected("all")
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Show All")
            }
        }
    }
}
