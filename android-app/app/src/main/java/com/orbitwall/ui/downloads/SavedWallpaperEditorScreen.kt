package com.orbitwall.ui.downloads

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

@Composable
fun SavedWallpaperEditorScreen(
    imageUriString: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showWallpaperOptions by remember { mutableStateOf(false) }
    var showWallpaperSet by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var wallpaperStatusMessage by remember { mutableStateOf<String?>(null) }
    
    // Load bitmap from URI
    LaunchedEffect(imageUriString) {
        isLoading = true
        try {
            val uri = Uri.parse(imageUriString)
            if (uri == Uri.EMPTY) {
                throw IllegalArgumentException("Invalid URI")
            }
            
            val loadedBitmap = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } catch (e: SecurityException) {
                    android.util.Log.e("SavedWallpaperEditor", "Security exception: ${e.message}", e)
                    null
                } catch (e: Exception) {
                    android.util.Log.e("SavedWallpaperEditor", "Error opening stream: ${e.message}", e)
                    null
                }
            }
            
            if (loadedBitmap != null) {
                bitmap = loadedBitmap
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load image. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SavedWallpaperEditor", "Error loading image: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (bitmap != null) {
            // Display the saved image
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Saved wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
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
            
            // Top bar with back button
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
                Text(
                    text = "Saved Wallpaper",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Status message
            wallpaperStatusMessage?.let { message ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 80.dp),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 8.dp
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Set Wallpaper button
            FloatingActionButton(
                onClick = {
                    if (!isProcessing && !showWallpaperSet) {
                        showWallpaperOptions = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Wallpaper,
                    contentDescription = "Set Wallpaper",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load image",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // Wallpaper Options Dialog
        if (showWallpaperOptions && bitmap != null) {
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
                                setSavedWallpaperToScreen(
                                    context = context,
                                    bitmap = bitmap!!,
                                    scope = scope,
                                    flag = WallpaperManager.FLAG_SYSTEM,
                                    setIsProcessing = { isProcessing = it },
                                    onComplete = {
                                        showWallpaperSet = true
                                        wallpaperStatusMessage = "✓ Wallpaper set to Home Screen!"
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Home Screen")
                        }
                        TextButton(
                            onClick = {
                                showWallpaperOptions = false
                                wallpaperStatusMessage = "Setting wallpaper..."
                                setSavedWallpaperToScreen(
                                    context = context,
                                    bitmap = bitmap!!,
                                    scope = scope,
                                    flag = WallpaperManager.FLAG_LOCK,
                                    setIsProcessing = { isProcessing = it },
                                    onComplete = {
                                        showWallpaperSet = true
                                        wallpaperStatusMessage = "✓ Wallpaper set to Lock Screen!"
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Lock Screen")
                        }
                        TextButton(
                            onClick = {
                                showWallpaperOptions = false
                                wallpaperStatusMessage = "Setting wallpaper..."
                                setSavedWallpaperToScreen(
                                    context = context,
                                    bitmap = bitmap!!,
                                    scope = scope,
                                    flag = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK,
                                    setIsProcessing = { isProcessing = it },
                                    onComplete = {
                                        showWallpaperSet = true
                                        wallpaperStatusMessage = "✓ Wallpaper set to Both Screens!"
                                    }
                                )
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
    
    // Auto-hide success message after 10 seconds
    wallpaperStatusMessage?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(10000)
            wallpaperStatusMessage = null
            showWallpaperSet = false
        }
    }
}

private fun setSavedWallpaperToScreen(
    context: Context,
    bitmap: Bitmap,
    scope: kotlinx.coroutines.CoroutineScope,
    flag: Int,
    setIsProcessing: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    scope.launch {
        setIsProcessing(true)
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            withContext(Dispatchers.IO) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, flag)
                    } else {
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
                Toast.makeText(context, "Failed to set wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            setIsProcessing(false)
        }
    }
}

