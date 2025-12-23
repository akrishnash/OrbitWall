package com.orbitwall.wallpaper

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.orbitwall.model.Region
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache for generated wallpapers to avoid regenerating them on every view.
 * Uses region ID + settings hash as key.
 */
object WallpaperCache {
    private val cache = ConcurrentHashMap<String, Bitmap>()
    private const val MAX_CACHE_SIZE = 10 // Keep last 10 wallpapers
    
    /**
     * Generate cache key from region and settings
     */
    private fun getCacheKey(region: Region, settingsHash: Int): String {
        return "${region.id}_${settingsHash}"
    }
    
    /**
     * Get cached wallpaper if available
     */
    fun get(region: Region, settingsHash: Int): ImageBitmap? {
        val key = getCacheKey(region, settingsHash)
        return cache[key]?.asImageBitmap()
    }
    
    /**
     * Store wallpaper in cache
     */
    fun put(region: Region, settingsHash: Int, bitmap: Bitmap) {
        val key = getCacheKey(region, settingsHash)
        
        // Limit cache size - remove oldest entries if needed
        if (cache.size >= MAX_CACHE_SIZE && !cache.containsKey(key)) {
            val firstKey = cache.keys.firstOrNull()
            firstKey?.let { cache.remove(it) }
        }
        
        cache[key] = bitmap
    }
    
    /**
     * Clear cache
     */
    fun clear() {
        cache.clear()
    }
    
    /**
     * Remove specific entry
     */
    fun remove(region: Region, settingsHash: Int) {
        val key = getCacheKey(region, settingsHash)
        cache.remove(key)
    }
}



