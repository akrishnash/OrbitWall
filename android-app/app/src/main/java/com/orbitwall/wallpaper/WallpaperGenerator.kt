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
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.roundToInt

object WallpaperGenerator {
    private const val TILE_SIZE = 256
    private const val MIN_ZOOM = 3
    private const val MAX_ZOOM = 19
    private val client = OkHttpClient.Builder().build()

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
        
        // Always use preview screen dimensions for geographic area calculation
        // This ensures we show the exact same area as the preview
        val renderScreen = previewScreen ?: screen
        
        // Calculate meters per pixel at this zoom level for the preview bitmap
        val metersPerPixel = TileMath.metersPerPixel(center.lat, z)
        
        // Calculate what geographic area is visible after UI transforms:
        // - The preview bitmap is at renderScreen size
        // - UI applies scale transform: when scale > 1 (zoomed in), we see LESS area
        // - UI applies pan transform: shifts which part is visible
        
        // Visible area in preview bitmap pixels (after scale transform)
        val visibleWidth = renderScreen.width.toDouble() / scale
        val visibleHeight = renderScreen.height.toDouble() / scale
        
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

        // Generate bitmap at PREVIEW resolution first (ensures perfect tile alignment, no striping)
        val previewBitmap = createBitmap(renderScreen.width, renderScreen.height, Bitmap.Config.ARGB_8888)
        val previewCanvas = Canvas(previewBitmap)
        previewCanvas.drawColor(Color.BLACK)

        // Draw tiles at native size - perfect alignment, no scaling artifacts
        val centerX = renderScreen.width / 2.0
        val centerY = renderScreen.height / 2.0
        
        for (x in minTileX..maxTileX) {
            for (y in minTileY..maxTileY) {
                val tile = fetchTile(x, y, z) ?: continue
                val tileOffsetX = (x - adjustedCenterX) * TILE_SIZE
                val tileOffsetY = (y - adjustedCenterY) * TILE_SIZE
                val drawX = (centerX + tileOffsetX).toFloat()
                val drawY = (centerY + tileOffsetY).toFloat()
                previewCanvas.drawBitmap(tile, drawX, drawY, null)
            }
        }

        // Apply effects to preview bitmap
        val previewWithEffects = applyEffects(previewBitmap, settings)

        // Scale the entire bitmap to output size (smooth, no stripes)
        val finalBitmap = if (screen.width != renderScreen.width || screen.height != renderScreen.height) {
            Bitmap.createScaledBitmap(previewWithEffects, screen.width, screen.height, true).also {
                // Clean up intermediate bitmaps
                previewBitmap.recycle()
                previewWithEffects.recycle()
            }
        } else {
            previewWithEffects.also {
                previewBitmap.recycle()
            }
        }

        finalBitmap
    }

    private fun fetchTile(x: Int, y: Int, z: Int): Bitmap? {
        val n = 1 shl z
        val normalizedX = ((x % n) + n) % n
        val normalizedY = y.coerceIn(0, n - 1)
        val url = TileUrlTemplate
            .replace("{z}", z.toString())
            .replace("{x}", normalizedX.toString())
            .replace("{y}", normalizedY.toString())

        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.byteStream()?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (_: IOException) {
            null
        }
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
