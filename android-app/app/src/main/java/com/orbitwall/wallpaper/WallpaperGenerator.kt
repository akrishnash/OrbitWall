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
        scale: Float = 1f
    ): Bitmap = withContext(Dispatchers.IO) {
        // Adjust zoom level based on scale (zoomed in = higher effective zoom)
        val adjustedZoom = (zoomLevel + kotlin.math.log2(scale.toDouble())).toInt().coerceIn(MIN_ZOOM, MAX_ZOOM)
        val z = adjustedZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
        
        // Calculate meters per pixel at the base zoom level (before scale adjustment)
        val baseMetersPerPixel = TileMath.metersPerPixel(center.lat, zoomLevel)
        
        // Calculate the actual pan offset in meters (accounting for scale)
        // Pan offset is in preview screen pixels, convert to actual geographic distance
        val panMetersX = -(panOffsetX / scale) * baseMetersPerPixel
        val panMetersY = (panOffsetY / scale) * baseMetersPerPixel // Y is inverted (positive Y = north)
        
        // Convert meters to degrees
        // 1 degree latitude ≈ 111,000 meters (constant)
        // 1 degree longitude ≈ 111,000 * cos(latitude) meters
        val metersPerDegreeLat = 111000.0
        val metersPerDegreeLon = 111000.0 * kotlin.math.cos(Math.toRadians(center.lat))
        
        val latOffset = panMetersY / metersPerDegreeLat
        val lonOffset = panMetersX / metersPerDegreeLon
        
        // Calculate the new center point after panning
        val adjustedCenter = GeoLocation(
            lat = center.lat + latOffset,
            lon = center.lon + lonOffset
        )
        
        // Calculate tile coordinates for adjusted center at adjusted zoom
        val (adjustedCenterX, adjustedCenterY) = TileMath.latLonToTilePoint(adjustedCenter.lat, adjustedCenter.lon, z)
        
        // Calculate tile bounds - scale determines how much area we need to render
        // When zoomed in (scale > 1), we need fewer tiles to fill the screen
        // When zoomed out (scale < 1), we need more tiles
        val screenTilesW = screen.width.toDouble() / TILE_SIZE / scale
        val screenTilesH = screen.height.toDouble() / TILE_SIZE / scale

        val minTileX = floor(adjustedCenterX - screenTilesW / 2).toInt()
        val maxTileX = ceil(adjustedCenterX + screenTilesW / 2).toInt()
        val minTileY = floor(adjustedCenterY - screenTilesH / 2).toInt()
        val maxTileY = ceil(adjustedCenterY + screenTilesH / 2).toInt()

        // Generate bitmap at the exact screen size (wallpaper manager will handle cropping)
        val bitmap = createBitmap(screen.width, screen.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)

        for (x in minTileX..maxTileX) {
            for (y in minTileY..maxTileY) {
                val tile = fetchTile(x, y, z) ?: continue
                // Calculate draw position relative to adjusted center
                val drawX = ((x - adjustedCenterX) * TILE_SIZE + screen.width / 2.0).toFloat()
                val drawY = ((y - adjustedCenterY) * TILE_SIZE + screen.height / 2.0).toFloat()
                canvas.drawBitmap(tile, drawX, drawY, null)
            }
        }

        applyEffects(bitmap, settings)
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
        return log2(ratio).roundTo