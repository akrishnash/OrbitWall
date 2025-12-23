package com.orbitwall.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

object PermissionHandler {
    /**
     * Check if storage permission is granted
     */
    fun hasStoragePermission(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ - need READ_MEDIA_IMAGES
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10-12 - scoped storage, no permission needed for saving to Downloads
                // But we check READ_EXTERNAL_STORAGE for reading
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // Android 9 and below - need WRITE_EXTERNAL_STORAGE
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
    
    /**
     * Get the permission string to request based on Android version
     */
    fun getStoragePermission(): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            else -> {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }
    }
    
    /**
     * Composable to handle permission request
     */
    @Composable
    fun rememberPermissionLauncher(
        onPermissionResult: (Boolean) -> Unit
    ): androidx.activity.result.ActivityResultLauncher<String> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult(isGranted)
        }
    }
}



