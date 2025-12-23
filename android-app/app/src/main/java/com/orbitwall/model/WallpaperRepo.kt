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
        
        // For "all" category, include all regions
        val allCategoryRegions = regionsWithPreviews
        
        Homepage(
            featured = regionsWithPreviews.firstOrNull() ?: regions.first(),
            categories = categories.map { category ->
                val categoryRegions = if (category.id == "all") {
                    allCategoryRegions
                } else {
                    // Match category.id with region tags (case-insensitive)
                    // Also handle singular/plural variations
                    regionsWithPreviews.filter { region ->
                        region.tags.any { tag ->
                            tag.equals(category.id, ignoreCase = true) ||
                            // Handle singular/plural matching
                            (category.id.endsWith("s") && tag.equals(category.id.dropLast(1), ignoreCase = true)) ||
                            (tag.endsWith("s") && tag.dropLast(1).equals(category.id, ignoreCase = true))
                        }
                    }
                }
                GalleryCategory(category.id, category.label, categoryRegions)
            }
        )
    }
}
