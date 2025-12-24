package com.orbitwall.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.orbitwall.model.Region
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache for generated wallpapers to avoid regenerating them on every view.
 * Uses region ID + settings hash as key.
 * Supports both in-memory and disk caching for persistence.
 */
object WallpaperCache {
    private val cache = ConcurrentHashMap<String, Bitmap>()
    private const val MAX_CACHE_SIZE = 10 // Keep last 10 wallpapers in memory
    private const val CACHE_DIR_NAME = "wallpaper_cache"
    private var cacheDir: File? = null
    
    /**
     * Initialize cache directory (call from Application or MainActivity)
     */
    fun init(context: Context) {
        cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        // Clean up old cache files on init (keep last 20)
        cleanupOldCacheFiles()
    }
    
    /**
     * Generate cache key from region and settings
     */
    private fun getCacheKey(region: Region, settingsHash: Int): String {
        return "${region.id}_${settingsHash}"
    }
    
    /**
     * Get cache file for a key
     */
    private fun getCacheFile(key: String): File? {
        return cacheDir?.let { File(it, "$key.jpg") }
    }
    
    /**
     * Get cached wallpaper if available (checks memory first, then disk)
     */
    suspend fun get(region: Region, settingsHash: Int): ImageBitmap? = withContext(Dispatchers.IO) {
        val key = getCacheKey(region, settingsHash)
        
        // Check memory cache first
        cache[key]?.let { return@withContext it.asImageBitmap() }
        
        // Check disk cache
        val cacheFile = getCacheFile(key)
        if (cacheFile != null && cacheFile.exists()) {
            try {
                val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath)
                if (bitmap != null && !bitmap.isRecycled) {
                    // Load into memory cache for faster access next time
                    if (cache.size < MAX_CACHE_SIZE) {
                        cache[key] = bitmap
                    }
                    return@withContext bitmap.asImageBitmap()
                }
            } catch (e: Exception) {
                android.util.Log.e("WallpaperCache", "Error loading from disk: ${e.message}", e)
                // Delete corrupted cache file
                cacheFile.delete()
            }
        }
        
        null
    }
    
    /**
     * Store wallpaper in cache (both memory and disk)
     */
    suspend fun put(region: Region, settingsHash: Int, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val key = getCacheKey(region, settingsHash)
        
        // Store in memory cache
        if (cache.size >= MAX_CACHE_SIZE && !cache.containsKey(key)) {
            val firstKey = cache.keys.firstOrNull()
            firstKey?.let { cache.remove(it) }
        }
        cache[key] = bitmap
        
        // Store on disk for persistence
        val cacheFile = getCacheFile(key)
        if (cacheFile != null) {
            try {
                FileOutputStream(cacheFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
            } catch (e: Exception) {
                android.util.Log.e("WallpaperCache", "Error saving to disk: ${e.message}", e)
            }
        }
    }
    
    /**
     * Clear cache (both memory and disk)
     */
    fun clear() {
        cache.clear()
        cacheDir?.listFiles()?.forEach { it.delete() }
    }
    
    /**
     * Remove specific entry
     */
    fun remove(region: Region, settingsHash: Int) {
        val key = getCacheKey(region, settingsHash)
        cache.remove(key)
        getCacheFile(key)?.delete()
    }
    
    /**
     * Clean up old cache files, keeping only the most recent ones
     */
    private fun cleanupOldCacheFiles() {
        cacheDir?.listFiles()?.let { files ->
            if (files.size > 20) { // Keep last 20 files
                files.sortedBy { it.lastModified() }
                    .take(files.size - 20)
                    .forEach { it.delete() }
            }
        }
    }
}




