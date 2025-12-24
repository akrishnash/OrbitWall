package com.orbitwall.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbitwall.ui.gallery.GalleryViewModel

@Composable
fun CategoryScreen(
    onCategorySelected: (String) -> Unit,
    galleryViewModel: GalleryViewModel = viewModel()
) {
    val homepage by galleryViewModel.homepage.collectAsState(null)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "About Regions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.dp.value.toInt().sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                Text(
                    text = "Explore satellite imagery from around the world. Each region showcases unique landscapes, landmarks, and natural wonders captured from space.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            homepage?.categories?.forEach { category ->
                if (category.id != "all") {
                    item {
                        CategoryCard(
                            categoryName = category.label,
                            regionCount = category.regions.size,
                            description = getCategoryDescription(category.id),
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    categoryName: String,
    regionCount: Int,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = categoryName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.dp.value.toInt().sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$regionCount wallpapers available",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.dp.value.toInt().sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.dp.value.toInt().sp
            )
        }
    }
}

private fun getCategoryDescription(categoryId: String): String {
    return when (categoryId) {
        "africa" -> "Discover the diverse landscapes of Africa, from deserts to mountains."
        "asia" -> "Explore the vast continent of Asia with its rich cultural and natural heritage."
        "europe" -> "Experience the beauty of European landscapes and historic landmarks."
        "north-america" -> "Journey through North America's stunning natural wonders."
        "south-america" -> "Discover the vibrant landscapes of South America."
        "oceania" -> "Explore the islands and coastlines of Oceania."
        "antarctica" -> "Witness the pristine ice landscapes of Antarctica."
        "landmarks" -> "Famous structures and monuments visible from space."
        "beaches" -> "Beautiful coastal areas and tropical paradises."
        "mountains" -> "Majestic peaks and mountain ranges around the world."
        "volcanoes" -> "Active and dormant volcanoes captured from above."
        "scenic" -> "Breathtaking natural landscapes and vistas."
        "islands" -> "Islands, atolls, and archipelagos from around the globe."
        "urban" -> "Cities and urban landscapes seen from space."
        "nature" -> "Natural wonders including forests, lakes, and wilderness."
        "desert" -> "Arid landscapes, dunes, and desert formations."
        "ocean" -> "Coastal views, reefs, and marine features."
        "ice" -> "Glaciers, polar regions, and frozen landscapes."
        else -> "Explore this category to discover unique satellite imagery."
    }
}

