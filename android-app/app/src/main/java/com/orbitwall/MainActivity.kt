package com.orbitwall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.orbitwall.ui.theme.OrbitWallTheme
import com.orbitwall.utils.PreferencesManager
import com.orbitwall.wallpaper.WallpaperCache

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize wallpaper cache
        WallpaperCache.init(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        setContent {
            val context = LocalContext.current
            val useSystemTheme = remember { PreferencesManager.useSystemTheme(context) }
            val darkModeEnabled = remember { PreferencesManager.isDarkModeEnabled(context) }
            val systemDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = if (useSystemTheme) systemDarkTheme else darkModeEnabled
            
            OrbitWallTheme(darkTheme = isDarkTheme) {
                OrbitWallApp()
            }
        }
    }
}

