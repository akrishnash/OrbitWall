package com.orbitwall.ui.editor

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orbitwall.model.DefaultSettings
import com.orbitwall.model.Dimensions
import com.orbitwall.model.GeoLocation
import com.orbitwall.model.Region
import com.orbitwall.ui.components.ControlsPanel
import com.orbitwall.wallpaper.WallpaperGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.roundToInt

@Composable
fun EditorScreen(
    region: Region,
    onBack: () -> Unit,
    allRegions: List<Region> = emptyList(),
    onRegionChanged: ((Region) -> Unit)? = null,
    previewImage: ImageBitmap? = null
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val screenDim = remember(configuration, density) {
        val widthPx = with(density) { configuration.screenWidthDp.dp.toPx() }.roundToInt()
        val heightPx = with(density) { configuration.screenHeightDp.dp.toPx() }.roundToInt()
        Dimensions(width = widthPx, height = heightPx)
    }

    var settings by remember(region.id) { mutableStateOf(DefaultSettings) }
    var preview by remember(region.id) { mutableStateOf(previewImage) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pinchScale by remember(region.id) { mutableStateOf(1f) }
    var panOffset by remember(region.id) { mutableStateOf(Offset.Zero) }
    var showWallpaperSet by remember { mutableStateOf(false) }
    var showCustomize by remember { mutableStateOf(false) }
    var showWallpaperOptions by remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var wallpaperStatusMessage by remember { mutableStateOf<String?>(null) }

    val effectiveZoom = remember(settings, region) { region.zoom + settings.zoomOffset }
    
    // Get current region index for navigation
    val currentIndex = remember(region.id, allRegions) {
        allRegions.indexOfFirst { it.id == region.id }.takeIf { it >= 0 } ?: 0
    }
    val hasNext = currentIndex < allRegions.size - 1
    val hasPrevious = currentIndex > 0
    
    fun goToNext() {
        if (hasNext) {
            onRegionChanged?.invoke(allRegions[currentIndex + 1])
            swipeOffset = 0f
            pinchScale = 1f
            panOffset = Offset.Zero
        }
    }
    
    fun goToPrevious() {
        if (hasPrevious) {
            onRegionChanged?.invoke(allRegions[currentIndex - 1])
            swipeOffset = 0f
            pinchScale = 1f
            panOffset = Offset.Zero
        }
    }

    suspend fun renderPreview(dimensions: Dimensions) {
        isProcessing = true
        errorMessage = null
        try {
            val bitmap = WallpaperGenerator.generateWallpaper(
                center = region.location,
                screen = dimensions,
                settings = settings,
                zoomLevel = effectiveZoom,
                panOffsetX = panOffset.x,
                panOffsetY = panOffset.y,
                scale = pinchScale
            )
            preview = bitmap.asImageBitmap()
        } catch (ex: Exception) {
            errorMessage = ex.message ?: "Failed to generate imagery"
        } finally {
            isProcessing = false
        }
    }

    // Initial render and reset pan/zoom when region changes
    LaunchedEffect(region.id) {
        pinchScale = 1f
        panOffset = Offset.Zero
        swipeOffset = 0f
        if (previewImage == null) {
            renderPreview(screenDim)
        } else {
            preview = previewImage
        }
    }

    // Auto-hide success message after 10 seconds
    wallpaperStatusMessage?.let {
        LaunchedEffect(it) {
            delay(10000)
            wallpaperStatusMessage = null
            showWallpaperSet = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (preview != null) {
            // Improved pan and zoom with bounds checking
            val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                val newScale = (pinchScale * zoomChange).coerceIn(0.5f, 4f)
                val scaleDelta = newScale / pinchScale
                
                // Calculate new pan offset with bounds based on scale
                val maxPanX = (preview!!.width * newScale - screenDim.width) / 2f
                val maxPanY = (preview!!.height * newScale - screenDim.height) / 2f
                
                panOffset = Offset(
                    x = (panOffset.x * scaleDelta + offsetChange.x).coerceIn(-maxPanX, maxPanX),
                    y = (panOffset.y * scaleDelta + offsetChange.y).coerceIn(-maxPanY, maxPanY)
                )
                pinchScale = newScale
            }
            
            // Combined gesture handling for pan, zoom, and swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // Handle swipe navigation
                                if (isDragging && !showCustomize && !showWallpaperOptions) {
                                    isDragging = false
                                    // Swipe left (drag left, negative offset) = next image
                                    // Swipe right (drag right, positive offset) = previous image
                                    if (swipeOffset < -150f && hasNext) {
                                        goToNext()
                                    } else if (swipeOffset > 150f && hasPrevious) {
                                        goToPrevious()
                                    } else {
                                        // Animate back to center
                                        swipeOffset = 0f
                                    }
                                } else {
                                    swipeOffset = 0f
                                }
                            }
                        ) { change, dragAmount ->
                            // Only handle swipe when not in customize mode and when scale is near 1
                            if (!showCustomize && !showWallpaperOptions && pinchScale < 1.1f) {
                                isDragging = true
                                swipeOffset += dragAmount
                                // Limit swipe offset
                                swipeOffset = swipeOffset.coerceIn(-500f, 500f)
                            }
                        }
                    }
                    .transformable(state = transformState)
            ) {
                Image(
                    bitmap = preview!!,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = pinchScale
                            scaleY = pinchScale
                            translationX = panOffset.x + swipeOffset
                            translationY = panOffset.y
                            // Add slight opacity fade for swipe feedback
                            alpha = if (isDragging && swipeOffset != 0f) {
                                1f - (kotlin.math.abs(swipeOffset) / 500f * 0.3f)
                            } else {
                                1f
                            }
                        }
                )
            }
        } else if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Text(
                text = "Could not load image",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                    0.2f to MaterialTheme.colorScheme.scrim.copy(alpha = 0.0f),
                    0.8f to MaterialTheme.colorScheme.scrim.copy(alpha = 0.0f),
                    1.0f to MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
                )
            )
        )

        // Simple top bar with back button and region name
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = region.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        errorMessage?.let {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Show wallpaper status message (success or progress)
        wallpaperStatusMessage?.let { message ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 80.dp), // Above buttons
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        text = if (isProcessing) "Setting wallpaper..." else message,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Simplified customize panel (only blur and exposure)
        AnimatedVisibility(
            visible = showCustomize,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customize",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showCustomize = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = "Done")
                        }
                    }
                    
                    // Blur control
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Blur Strength",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${settings.blur.roundToInt()}px",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        androidx.compose.material3.Slider(
                            value = settings.blur,
                            onValueChange = { 
                                if (!isProcessing) {
                                    settings = settings.copy(blur = it)
                                }
                            },
                            onValueChangeFinished = {
                                // Update preview when slider is released
                                scope.launch { 
                                    renderPreview(screenDim)
                                }
                            },
                            valueRange = 0f..20f,
                            steps = 19
                        )
                    }
                    
                    // Exposure/Brightness control
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Exposure",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(settings.brightness * 100).roundToInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        androidx.compose.material3.Slider(
                            value = settings.brightness,
                            onValueChange = { 
                                if (!isProcessing) {
                                    settings = settings.copy(brightness = it)
                                }
                            },
                            onValueChangeFinished = {
                                // Update preview when slider is released
                                scope.launch { 
                                    renderPreview(screenDim)
                                }
                            },
                            valueRange = 0.5f..1.5f,
                            steps = 19
                        )
                    }
                }
            }
        }

        // Simple two-button bottom bar (only show when customize panel is hidden)
        if (!showCustomize) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Customize button
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    onClick = { showCustomize = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune, 
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(text = "Customize")
                }
                
                // Set Wallpaper button - opens options dialog
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing && !showWallpaperSet,
                    onClick = {
                        showWallpaperOptions = true
                    }
                ) {
                    if (showWallpaperSet) {
                        Text(text = "✓ Set!")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Wallpaper, 
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(text = "Set Wallpaper")
                    }
                }
            }
        }
        
        // Wallpaper Options Dialog
        if (showWallpaperOptions) {
            AlertDialog(
                onDismissRequest = { showWallpaperOptions = false },
                title = { Text(text = "Set Wallpaper") },
                text = {
                    Column {
                        Text(
                            text = "Choose where to set the wallpaper:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        TextButton(
                            onClick = {
                                showWallpaperOptions = false
                                wallpaperStatusMessage = "Setting wallpaper..."
                                setWallpaperToScreen(context, region, settings, screenDim, effectiveZoom, 
                                    WallpaperManager.FLAG_SYSTEM, scope, { isProcessing = it }, 
                                    panOffset.x, panOffset.y, pinchScale) {
                                    showWallpaperSet = true
                                    wallpaperStatusMessage = "✓ Wallpaper set to Home Screen!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Home Screen")
                        }
                        TextButton(
                            onClick = {
                                showWallpaperOptions = false
                                wallpaperStatusMessage = "Setting wallpaper..."
                                setWallpaperToScreen(context, region, settings, screenDim, effectiveZoom,
                                    WallpaperManager.FLAG_LOCK, scope, { isProcessing = it },
                                    panOffset.x, panOffset.y, pinchScale) {
                                    showWallpaperSet = true
                                    wallpaperStatusMessage = "✓ Wallpaper set to Lock Screen!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Lock Screen")
                        }
                        TextButton(
                            onClick = {
                                showWallpaperOptions = false
                                wallpaperStatusMessage = "Setting wallpaper..."
                                setWallpaperToScreen(context, region, settings, screenDim, effectiveZoom,
                                    WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK, scope, { isProcessing = it },
                                    panOffset.x, panOffset.y, pinchScale) {
                                    showWallpaperSet = true
                                    wallpaperStatusMessage = "✓ Wallpaper set to Both Screens!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Both Screens")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showWallpaperOptions = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    val region = Region(
        id = "preview",
        name = "Preview Region",
        location = GeoLocation(0.0, 0.0),
        zoom = 10,
        tags = listOf("preview", "tag")
    )
    val placeholderBitmap = remember {
        Bitmap.createBitmap(400, 800, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.parseColor("#101020"))
        }.asImageBitmap()
    }
    EditorScreen(region = region, onBack = {}, allRegions = listOf(region), previewImage = placeholderBitmap)
}

private suspend fun saveToGallery(context: Context, bitmap: Bitmap, name: String): Uri? {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OrbitWall")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, stream)
            }
        }
        uri
    }
}

private fun setWallpaperToScreen(
    context: Context,
    region: Region,
    settings: com.orbitwall.model.WallSettings,
    screenDim: Dimensions,
    effectiveZoom: Int,
    flags: Int,
    scope: kotlinx.coroutines.CoroutineScope,
    setIsProcessing: (Boolean) -> Unit,
    panOffsetX: Float,
    panOffsetY: Float,
    scale: Float,
    onComplete: () -> Unit
) {
    scope.launch {
        setIsProcessing(true)
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val outputDim = Dimensions(
                wallpaperManager.desiredMinimumWidth, 
                wallpaperManager.desiredMinimumHeight
            )
            // Generate wallpaper accounting for pan and zoom
            val bitmap = WallpaperGenerator.generateWallpaper(
                center = region.location,
                screen = outputDim,
                settings = settings,
                zoomLevel = effectiveZoom + WallpaperGenerator.zoomCorrection(screenDim, outputDim),
                panOffsetX = panOffsetX,
                panOffsetY = panOffsetY,
                scale = scale
            )
            
            withContext(Dispatchers.IO) {
                try {
                    // Use setBitmap with flags for API 24+ (Android 7.0+)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, flags)
                    } else {
                        // Fallback for older versions - sets home screen only
                        wallpaperManager.setBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            onComplete()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to generate wallpaper", Toast.LENGTH_SHORT).show()
            }
            setIsProcessing(false)
        }
    }
}
