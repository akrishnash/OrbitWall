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
import com.orbitwall.utils.PermissionHandler
import com.orbitwall.utils.PreferencesManager
import com.orbitwall.wallpaper.WallpaperCache
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

    // Load saved state if available
    val savedState = remember(region.id) {
        PreferencesManager.getEditorState(context)
    }
    
    var settings by remember(region.id) { 
        mutableStateOf(
            if (savedState != null) {
                DefaultSettings.copy(
                    zoomOffset = savedState.zoomOffset,
                    brightness = savedState.brightness,
                    blur = savedState.blur,
                    overlayOpacity = savedState.overlayOpacity
                )
            } else {
                DefaultSettings
            }
        )
    }
    var preview by remember(region.id) { mutableStateOf(previewImage) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pinchScale by remember(region.id) { 
        mutableStateOf(savedState?.pinchScale ?: 1f) 
    }
    var panOffset by remember(region.id) { 
        mutableStateOf(
            if (savedState != null) {
                Offset(savedState.panOffsetX, savedState.panOffsetY)
            } else {
                Offset.Zero
            }
        )
    }
    var showWallpaperSet by remember { mutableStateOf(false) }
    var showWallpaperOptions by remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var wallpaperStatusMessage by remember { mutableStateOf<String?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Use lower zoom to match gallery preview - start at zoom 7-8 for faster loading
    // Gallery uses zoom 5, so editor at 7-8 is a good middle ground
    val effectiveZoom = remember(settings, region) { 
        // Start with lower zoom (7-8) to match gallery preview better and load faster
        // User can adjust zoom in settings if needed
        (region.zoom + settings.zoomOffset).coerceIn(7, 12) // Lower initial zoom for faster loading
    }
    
    // Permission launcher
    val permissionLauncher = PermissionHandler.rememberPermissionLauncher { isGranted ->
        if (isGranted) {
            // Permission granted, retry save operation
            scope.launch {
                isProcessing = true
                try {
                    val bitmapToSave = WallpaperGenerator.generateWallpaper(
                        center = region.location,
                        screen = screenDim,
                        settings = settings,
                        zoomLevel = effectiveZoom,
                        panOffsetX = panOffset.x,
                        panOffsetY = panOffset.y,
                        scale = pinchScale,
                        previewScreen = screenDim
                    )
                    val cleanName = region.name.replace(" ", "_")
                        .replace("/", "_")
                        .replace("\\", "_")
                        .replace(":", "_")
                        .replace("*", "_")
                        .replace("?", "_")
                        .replace("\"", "_")
                        .replace("<", "_")
                        .replace(">", "_")
                        .replace("|", "_")
                    val fileName = "${cleanName}_${System.currentTimeMillis()}.jpg"
                    val result = saveToGallery(context, bitmapToSave, fileName)
                    withContext(Dispatchers.Main) {
                        if (result.success) {
                            Toast.makeText(context, "Wallpaper saved to Pictures/OrbitWall", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to save: ${result.error}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: ${e.message ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isProcessing = false
                }
            }
        } else {
            Toast.makeText(context, "Permission denied. Cannot save wallpaper.", Toast.LENGTH_LONG).show()
        }
        showPermissionDialog = false
    }
    
    // Settings hash for cache key
    val settingsHash = remember(settings) {
        settings.hashCode()
    }
    
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
            // Check cache first (now supports disk cache)
            val cached = WallpaperCache.get(region, settingsHash)
            if (cached != null) {
                preview = cached
                isProcessing = false
                return
            }
            
            // Generate preview WITHOUT pan/scale - UI will apply them via graphicsLayer
            // This ensures the preview bitmap matches what we'll generate for wallpaper
            val bitmap = WallpaperGenerator.generateWallpaper(
                center = region.location,
                screen = dimensions,
                settings = settings,
                zoomLevel = effectiveZoom,
                panOffsetX = 0f, // Don't apply pan here - UI handles it
                panOffsetY = 0f,
                scale = 1f // Don't apply scale here - UI handles it
            )
            preview = bitmap.asImageBitmap()
            
            // Cache the generated bitmap (now saves to disk for persistence)
            WallpaperCache.put(region, settingsHash, bitmap)
        } catch (ex: Exception) {
            errorMessage = ex.message ?: "Failed to generate imagery"
        } finally {
            isProcessing = false
        }
    }

    // Save state when it changes (debounced to avoid too frequent saves)
    LaunchedEffect(region.id, pinchScale, panOffset.x, panOffset.y, settings.zoomOffset, settings.brightness, settings.blur, settings.overlayOpacity) {
        kotlinx.coroutines.delay(500) // Debounce saves
        PreferencesManager.saveEditorState(
            context = context,
            pinchScale = pinchScale,
            panOffsetX = panOffset.x,
            panOffsetY = panOffset.y,
            zoomOffset = settings.zoomOffset,
            brightness = settings.brightness,
            blur = settings.blur,
            overlayOpacity = settings.overlayOpacity
        )
        PreferencesManager.saveLastRegionId(context, region.id)
    }
    
    // Initial render and reset pan/zoom when region changes (only if no saved state)
    LaunchedEffect(region.id) {
        if (savedState == null) {
            pinchScale = 1f
            panOffset = Offset.Zero
        }
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
                try {
                    // Prevent division by zero and invalid scales
                    if (pinchScale <= 0f || zoomChange <= 0f) return@rememberTransformableState
                    
                    val newScale = (pinchScale * zoomChange).coerceIn(0.5f, 4f)
                    val scaleDelta = if (pinchScale > 0f) newScale / pinchScale else 1f
                    
                    // Calculate new pan offset with bounds based on scale
                    val maxPanX = (preview!!.width * newScale - screenDim.width) / 2f
                    val maxPanY = (preview!!.height * newScale - screenDim.height) / 2f
                    
                    panOffset = Offset(
                        x = (panOffset.x * scaleDelta + offsetChange.x).coerceIn(-maxPanX, maxPanX),
                        y = (panOffset.y * scaleDelta + offsetChange.y).coerceIn(-maxPanY, maxPanY)
                    )
                    pinchScale = newScale
                } catch (e: Exception) {
                    // Prevent crashes from invalid transform operations
                    android.util.Log.e("EditorScreen", "Transform error: ${e.message}", e)
                }
            }
            
            // Combined gesture handling for pan, zoom, and swipe
            // Use transformable for pinch zoom/pan, and separate pointerInput for swipe
            // Only enable swipe when not zoomed in
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformState)
                    .pointerInput(pinchScale) {
                        // Only detect horizontal drag for swipe when scale is near 1 (not zoomed)
                        if (pinchScale < 1.15f) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // Handle swipe navigation
                                    if (isDragging && !showWallpaperOptions && pinchScale < 1.15f) {
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
                                // Only handle swipe when not zoomed and not showing dialog
                                if (!showWallpaperOptions && pinchScale < 1.15f) {
                                isDragging = true
                                swipeOffset += dragAmount
                                // Limit swipe offset
                                swipeOffset = swipeOffset.coerceIn(-500f, 500f)
                            }
                        }
                    }
                    }
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
                // Add background for better readability on dark images
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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

        // Two icon buttons: Save and Set Wallpaper
        Row(
                modifier = Modifier
                .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
            // Save button (Download icon)
            FloatingActionButton(
                onClick = {
                    // Check permission first
                    if (PermissionHandler.hasStoragePermission(context)) {
                        // Permission already granted, proceed with save
                        scope.launch {
                            isProcessing = true
                            try {
                                // Generate wallpaper for saving (cache will speed up if recently viewed)
                                val bitmapToSave = WallpaperGenerator.generateWallpaper(
                                    center = region.location,
                                    screen = screenDim,
                                    settings = settings,
                                    zoomLevel = effectiveZoom,
                                    panOffsetX = panOffset.x,
                                    panOffsetY = panOffset.y,
                                    scale = pinchScale,
                                    previewScreen = screenDim
                                )
                                
                                // Clean filename - remove invalid characters
                                val cleanName = region.name.replace(" ", "_")
                                    .replace("/", "_")
                                    .replace("\\", "_")
                                    .replace(":", "_")
                                    .replace("*", "_")
                                    .replace("?", "_")
                                    .replace("\"", "_")
                                    .replace("<", "_")
                                    .replace(">", "_")
                                    .replace("|", "_")
                                val fileName = "${cleanName}_${System.currentTimeMillis()}.jpg"
                                
                                val result = saveToGallery(context, bitmapToSave, fileName)
                                
                                withContext(Dispatchers.Main) {
                                    if (result.success) {
                                        Toast.makeText(context, "Wallpaper saved to Pictures/OrbitWall", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save: ${result.error}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Error: ${e.message ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                                }
                            } finally {
                                isProcessing = false
                            }
                        }
                    } else {
                        // Request permission
                        showPermissionDialog = true
                    }
                },
                modifier = Modifier.size(56.dp),
                        containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Save",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Set Wallpaper button
            FloatingActionButton(
                    onClick = {
                    if (!isProcessing && !showWallpaperSet) {
                        showWallpaperOptions = true
                    }
                },
                modifier = Modifier.size(56.dp)
                ) {
                    if (showWallpaperSet) {
                    Icon(
                        imageVector = Icons.Filled.Wallpaper,
                        contentDescription = "Set",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Wallpaper, 
                        contentDescription = "Set Wallpaper",
                        modifier = Modifier.size(24.dp)
                        )
                }
            }
        }
        
        // Permission Request Dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text(text = "Storage Permission Required") },
                text = {
                    Column {
                        Text(
                            text = "OrbitWall needs permission to save wallpapers to your device. This allows you to save and view your favorite wallpapers.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            permissionLauncher.launch(PermissionHandler.getStoragePermission())
                        }
                    ) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
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

// Result class for save operation
private data class SaveResult(val success: Boolean, val uri: Uri? = null, val error: String = "")

private suspend fun saveToGallery(context: Context, bitmap: Bitmap, name: String): SaveResult {
    return withContext(Dispatchers.IO) {
        try {
        val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                // Save to Pictures/OrbitWall folder (Android 10+) - more reliable than Downloads
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${android.os.Environment.DIRECTORY_PICTURES}/OrbitWall")
                }
            }
            
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri == null) {
                return@withContext SaveResult(false, error = "Failed to create MediaStore entry")
            }
            
            resolver.openOutputStream(uri)?.use { stream ->
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    stream.flush()
                    return@withContext SaveResult(true, uri)
                } else {
                    // Compression failed, try to delete the created entry
                    try {
                        resolver.delete(uri, null, null)
                    } catch (e: Exception) {
                        // Ignore delete errors
                    }
                    return@withContext SaveResult(false, error = "Failed to compress image")
                }
            } ?: run {
                // Failed to open output stream, try to delete the created entry
                try {
                    resolver.delete(uri, null, null)
                } catch (e: Exception) {
                    // Ignore delete errors
                }
                return@withContext SaveResult(false, error = "Failed to open output stream. Check storage permissions.")
            }
        } catch (e: SecurityException) {
            android.util.Log.e("SaveWallpaper", "Security exception: ${e.message}", e)
            return@withContext SaveResult(false, error = "Permission denied: ${e.message}")
        } catch (e: Exception) {
            android.util.Log.e("SaveWallpaper", "Error saving wallpaper: ${e.message}", e)
            return@withContext SaveResult(false, error = e.message ?: "Unknown error")
        }
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
            // Generate wallpaper at higher resolution but SAME zoom/pan/scale as preview
            // Pass preview screen dimensions to ensure we show the exact same geographic area
            // No zoom correction - we want the exact same geographic area, just rendered at higher resolution
            val bitmap = WallpaperGenerator.generateWallpaper(
                center = region.location,
                screen = outputDim, // Output at wallpaper resolution
                settings = settings,
                zoomLevel = effectiveZoom, // Same zoom as preview - no correction
                panOffsetX = panOffsetX,
                panOffsetY = panOffsetY,
                scale = scale,
                previewScreen = screenDim // Use preview dimensions for geographic area calculation
            )
            
            withContext(Dispatchers.IO) {
                try {
                    // Ensure bitmap is in a compatible format - some devices require specific formats
                    // Create a copy in ARGB_8888 format to ensure compatibility
                    val compatibleBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
                        bitmap.copy(Bitmap.Config.ARGB_8888, false)
                    } else {
                        bitmap
                    }
                    
                    // For better compatibility, especially on some devices that show black screens,
                    // ensure the bitmap is not recycled and has valid dimensions
                    if (compatibleBitmap.isRecycled || compatibleBitmap.width <= 0 || compatibleBitmap.height <= 0) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Invalid bitmap format", Toast.LENGTH_SHORT).show()
                        }
                        setIsProcessing(false)
                        return@withContext
                    }
                    
                    // Use setBitmap with flags for API 24+ (Android 7.0+)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(compatibleBitmap, null, true, flags)
                    } else {
                        // Fallback for older versions - sets home screen only
                        wallpaperManager.setBitmap(compatibleBitmap)
                    }
                    
                    // Clean up if we created a copy
                    if (compatibleBitmap != bitmap) {
                        compatibleBitmap.recycle()
                    }
                } catch (e: IOException) {
                    android.util.Log.e("Wallpaper", "Failed to set wallpaper: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to set wallpaper: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("Wallpaper", "Unexpected error setting wallpaper: ${e.message}", e)
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
