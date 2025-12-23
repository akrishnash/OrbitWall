package com.orbitwall.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitwall.model.Homepage
import com.orbitwall.model.WallpaperRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    private val _homepage = MutableStateFlow<Homepage?>(null)
    val homepage: StateFlow<Homepage?> = _homepage

    init {
        viewModelScope.launch {
            try {
                // Load immediately
                _homepage.value = WallpaperRepo.getHomepage()
                android.util.Log.d("OrbitWall", "Homepage loaded: ${_homepage.value?.featured?.name}")
            } catch (e: Exception) {
                // Log error but don't crash - homepage will remain null
                android.util.Log.e("OrbitWall", "Error loading homepage", e)
                e.printStackTrace()
                // Set a fallback homepage with at least one region
                try {
                    val fallbackRegion = com.orbitwall.model.PredefinedRegions.firstOrNull()
                    if (fallbackRegion != null) {
                        _homepage.value = com.orbitwall.model.Homepage(
                            featured = fallbackRegion,
                            categories = listOf(
                                com.orbitwall.model.GalleryCategory("all", "All", com.orbitwall.model.PredefinedRegions)
                            )
                        )
                    }
                } catch (fallbackError: Exception) {
                    android.util.Log.e("OrbitWall", "Fallback also failed", fallbackError)
                }
            }
        }
    }
}
