package com.orbitwall.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import com.orbitwall.model.Dimensions
import com.orbitwall.model.GeoLocation
import com.orbitwall.model.Resolution
import com.orbitwall.model.TileUrlTemplate
import com.orbitwall.model.WallSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.roundToInt

object WallpaperGenerator {
    private const val TILE_SIZE = 256
    private const val MIN_ZOOM = 3
    private const val MAX_ZOOM = 19
    private const val MAX_PARALLEL_REQUESTS = 10 // Limit concurrent requests
    private const val CACHE_SIZE = 50 * 1024 * 1024L // 50MB cache
    
    // In-memory tile cache (simple LRU would be better, but this works)
    private val tileCache = ConcurrentHashMap<String, Bitmap>()
    
    // Optimized OkHttpClient with cache and connection pooling
    private val client = OkHttpClient.Builder()
        .cache(Cache(File(System.getProperty("java.io.tmpdir"), "orbitwall_tiles"), CACHE_SIZE))
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateWallpaper(
        center: GeoLocation,
        screen: Dimensions,
        settings: WallSettings,
        zoomLevel: Int,
        panOffsetX: Float = 0f,
        panOffsetY: Float = 0f,
        scale: Float = 1f,
        previewScreen: Dimensions? = null // Reference screen for geographic area calculation
    ): Bitmap = withContext(Dispatchers.IO) {
        // Use the exact zoom level - no adjustments
        val z = zoomLevel.coerceIn(MIN_ZOOM, MAX_ZOOM)
        
        // Calculate geographic area based on what the user sees in the preview
        // If previewScreen is provided, use it to determine the visible area
        // Otherwise, use the output screen dimensions
        val referenceScreen = previewScreen ?: screen
        
        // Calculate meters per pixel at this zoom level
        val metersPerPixel = TileMath.metersPerPixel(center.lat, z)
        
        // Calculate what geographic area is visible after UI transforms:
        // - The preview shows an area at referenceScreen size
        // - UI applies scale transform: when scale > 1 (zoomed in), we see LESS area
        // - UI applies pan transform: shifts which part is visible
        
        // Visible area in reference screen pixels (after scale transform)
        // This is the geographic area the user sees in the preview
        val visibleWidth = referenceScreen.width.toDouble() / scale
        val visibleHeight = referenceScreen.height.toDouble() / scale
        
        // Pan offset is in preview bitmap pixels
        // Convert to geographic distance
        val panMetersX = -panOffsetX * metersPerPixel
        val panMetersY = panOffsetY * metersPerPixel // Y is inverted (positive Y = north)
        
        // Convert meters to degrees
        val metersPerDegreeLat = 111000.0
        val metersPerDegreeLon = 111000.0 * kotlin.math.cos(Math.toRadians(center.lat))
        
        val latOffset = panMetersY / metersPerDegreeLat
        val lonOffset = panMetersX / metersPerDegreeLon
        
        // Calculate the new center point after panning
        val adjustedCenter = GeoLocation(
            lat = center.lat + latOffset,
            lon = center.lon + lonOffset
        )
        
        // Calculate tile coordinates for adjusted center
        val (adjustedCenterX, adjustedCenterY) = TileMath.latLonToTilePoint(adjustedCenter.lat, adjustedCenter.lon, z)
        
        // Calculate tile bounds based on visible area
        val geographicAreaTilesW = visibleWidth / TILE_SIZE
        val geographicAreaTilesH = visibleHeight / TILE_SIZE

        val minTileX = floor(adjustedCenterX - geographicAreaTilesW / 2).toInt()
        val maxTileX = ceil(adjustedCenterX + geographicAreaTilesW / 2).toInt()
        val minTileY = floor(adjustedCenterY - geographicAreaTilesH / 2).toInt()
        val maxTileY = ceil(adjustedCenterY + geographicAreaTilesH / 2).toInt()

        // Calculate the scale factor needed to render the visible geographic area
        // at the output screen dimensions while maintaining the same geographic coverage
        val scaleX = screen.width.toDouble() / visibleWidth
        val scaleY = screen.height.toDouble() / visibleHeight
        
        // Use uniform scale (min) to maintain aspect ratio and prevent stretching
        // This ensures the exact same geographic area is shown, matching the preview
        // Using minOf prevents stretching - the image will maintain correct aspect ratio
        // If preview and wallpaper have different aspect ratios, the image will be centered
        // with potential black bars (letterboxing/pillarboxing), but no distortion
        val tileScale = minOf(scaleX, scaleY)
        
        // Calculate the actual render dimensions that will show the visible area
        // We'll render at output dimensions, but scale tiles appropriately
        val renderWidth = screen.width
        val renderHeight = screen.height
        
        // Generate bitmap at output resolution
        val renderBitmap = createBitmap(renderWidth, renderHeight, Bitmap.Config.ARGB_8888)
        val renderCanvas = Canvas(renderBitmap)
        renderCanvas.drawColor(Color.BLACK)

        // Draw tiles scaled to match the output dimensions
        // Center the visible area in the render bitmap
        val centerX = renderWidth / 2.0
        val centerY = renderHeight / 2.0
        
        // Collect all tile coordinates with scaling applied
        val tileCoordinates = mutableListOf<Triple<Int, Int, Pair<Double, Double>>>()
        for (x in minTileX..maxTileX) {
            for (y in minTileY..maxTileY) {
                // Calculate tile position in the visible area coordinate system
                val tileOffsetX = (x - adjustedCenterX) * TILE_SIZE
                val tileOffsetY = (y - adjustedCenterY) * TILE_SIZE
                // Scale to output dimensions
                val drawX = centerX + tileOffsetX * tileScale
                val drawY = centerY + tileOffsetY * tileScale
                tileCoordinates.add(Triple(x, y, drawX to drawY))
            }
        }
        
        // Fetch all tiles in parallel for MUCH faster loading
        val tiles = coroutineScope {
            tileCoordinates.chunked(MAX_PARALLEL_REQUESTS).flatMap { chunk ->
                chunk.map { (x, y, drawPos) ->
                    async { 
                        val tile = fetchTile(x, y, z)
                        Pair(drawPos, tile)
                    }
                }.awaitAll()
            }
        }
        
        // Draw all fetched tiles with scaling
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        tiles.forEach { tileData ->
            val (drawPos, tile) = tileData
            val (drawX, drawY) = drawPos
            if (tile != null) {
                // Scale the tile to match the output resolution
                val scaledTileSize = (TILE_SIZE * tileScale).toFloat()
                val srcRect = android.graphics.Rect(0, 0, tile.width, tile.height)
                val dstRect = android.graphics.RectF(
                    drawX.toFloat(),
                    drawY.toFloat(),
                    drawX.toFloat() + scaledTileSize,
                    drawY.toFloat() + scaledTileSize
                )
                renderCanvas.drawBitmap(tile, srcRect, dstRect, paint)
            }
        }

        // Apply effects to render bitmap
        val renderWithEffects = applyEffects(renderBitmap, settings)

        // Bitmap is already at output size, no scaling needed
        val finalBitmap = renderWithEffects

        finalBitmap
    }

    private suspend fun fetchTile(x: Int, y: Int, z: Int): Bitmap? = withContext(Dispatchers.IO) {
        val n = 1 shl z
        val normalizedX = ((x % n) + n) % n
        val normalizedY = y.coerceIn(0, n - 1)
        val cacheKey = "${z}_${normalizedX}_${normalizedY}"
        
        // Check cache first
        tileCache[cacheKey]?.let { return@withContext it }
        
        val url = TileUrlTemplate
            .replace("{z}", z.toString())
            .replace("{x}", normalizedX.toString())
            .replace("{y}", normalizedY.toString())

        val request = Request.Builder()
            .url(url)
            .cacheControl(okhttp3.CacheControl.Builder().maxAge(7, TimeUnit.DAYS).build())
            .build()
            
        return@withContext try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                response.body?.byteStream()?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    // Cache the bitmap (simple cache - in production use LRU cache)
                    if (bitmap != null && tileCache.size < 200) { // Limit cache size
                        tileCache[cacheKey] = bitmap
                    }
                    bitmap
                }
            }
        } catch (e: IOException) {
            null
        }
    }
    
    /**
     * Clear tile cache to free memory
     */
    fun clearCache() {
        tileCache.values.forEach { if (!it.isRecycled) it.recycle() }
        tileCache.clear()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyEffects(source: Bitmap, settings: WallSettings): Bitmap {
        val output = createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val matrix = ColorMatrix().apply {
            setScale(settings.brightness, settings.brightness, settings.brightness, 1f)
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && settings.blur > 0f) {
            // paint.setRenderEffect(RenderEffect.createBlurEffect(settings.blur, settings.blur, Shader.TileMode.CLAMP))
        }

        canvas.drawBitmap(source, 0f, 0f, paint)

        if (settings.overlayOpacity > 0f) {
            val overlayPaint = Paint().apply {
                color = (settings.overlayColor and 0xFFFFFFFF).toInt()
                alpha = (settings.overlayOpacity * 255).toInt().coerceIn(0, 255)
            }
            canvas.drawRect(
                0f,
                0f,
                output.width.toFloat(),
                output.height.toFloat(),
                overlayPaint
            )
        }
        return output
    }

    fun targetDimensions(resolution: Resolution, screen: Dimensions): Dimensions {
        if (resolution == Resolution.SCREEN) return screen
        val aspect = screen.width.toFloat() / screen.height
        val isPortrait = screen.height >= screen.width
        val width = if (isPortrait) {
            resolution.portraitWidth
        } else {
            resolution.landscapeWidth
        }
        val height = (width / aspect).roundToInt().coerceAtLeast(1080)
        return Dimensions(width = width, height = height)
    }

    fun zoomCorrection(original: Dimensions, target: Dimensions): Int {
        val ratio = target.width.toDouble() / original.width.toDouble()
        return log2(ratio).roundToInt()
    }
}
