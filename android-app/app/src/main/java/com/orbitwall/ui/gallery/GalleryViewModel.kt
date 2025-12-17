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
                _homepage.value = WallpaperRepo.getHomepage()
            } catch (e: Exception) {
                // Log error but don't crash - homepage will remain null
                e.printStackTrace()
            }
        }
    }
}
