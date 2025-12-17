package com.orbitwall.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Homepage(
    val featured: Region,
    val categories: List<GalleryCategory>
)

object WallpaperRepo {
    private val categories = PredefinedCategories
    private val regions = PredefinedRegions

    suspend fun getHomepage(): Homepage = withContext(Dispatchers.IO) {
        // Generate preview URLs for all regions if they don't have heroImage
        val regionsWithPreviews = regions.map { region ->
            try {
                if (region.heroImage.isNullOrBlank()) {
                    region.copy(heroImage = RegionHelper.getPreviewImageUrl(region))
                } else {
                    region
                }
            } catch (e: Exception) {
                // If preview generation fails, use a placeholder or null
                region.copy(heroImage = null)
            }
        }
        
        Homepage(
            featured = regionsWithPreviews.firstOrNull() ?: regions.first(),
            categories = categories.map { category ->
                val categoryRegions = regionsWithPreviews.filter { region ->
                    category.id == "all" || region.tags.any { it.equals(category.id, ignoreCase = true) }
                }
                GalleryCategory(category.id, category.label, categoryRegions)
            }
        )
    }
}
