package com.orbitwall.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "orbitwall_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_USE_SYSTEM_THEME = "use_system_theme"
    private const val KEY_LAST_REGION_ID = "last_region_id"
    private const val KEY_PINCH_SCALE = "pinch_scale"
    private const val KEY_PAN_OFFSET_X = "pan_offset_x"
    private const val KEY_PAN_OFFSET_Y = "pan_offset_y"
    private const val KEY_SETTINGS_ZOOM_OFFSET = "settings_zoom_offset"
    private const val KEY_SETTINGS_BRIGHTNESS = "settings_brightness"
    private const val KEY_SETTINGS_BLUR = "settings_blur"
    private const val KEY_SETTINGS_OVERLAY_OPACITY = "settings_overlay_opacity"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Dark mode preferences
    fun isDarkModeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DARK_MODE, false)
    }
    
    fun setDarkMode(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    
    fun useSystemTheme(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_USE_SYSTEM_THEME, true)
    }
    
    fun setUseSystemTheme(context: Context, use: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_USE_SYSTEM_THEME, use).apply()
    }
    
    // Editor state persistence
    fun saveLastRegionId(context: Context, regionId: String) {
        getPrefs(context).edit().putString(KEY_LAST_REGION_ID, regionId).apply()
    }
    
    fun getLastRegionId(context: Context): String? {
        return getPrefs(context).getString(KEY_LAST_REGION_ID, null)
    }
    
    fun saveEditorState(
        context: Context,
        pinchScale: Float,
        panOffsetX: Float,
        panOffsetY: Float,
        zoomOffset: Int,
        brightness: Float,
        blur: Float,
        overlayOpacity: Float
    ) {
        getPrefs(context).edit().apply {
            putFloat(KEY_PINCH_SCALE, pinchScale)
            putFloat(KEY_PAN_OFFSET_X, panOffsetX)
            putFloat(KEY_PAN_OFFSET_Y, panOffsetY)
            putInt(KEY_SETTINGS_ZOOM_OFFSET, zoomOffset)
            putFloat(KEY_SETTINGS_BRIGHTNESS, brightness)
            putFloat(KEY_SETTINGS_BLUR, blur)
            putFloat(KEY_SETTINGS_OVERLAY_OPACITY, overlayOpacity)
            apply()
        }
    }
    
    fun getEditorState(context: Context): EditorState? {
        val prefs = getPrefs(context)
        if (!prefs.contains(KEY_PINCH_SCALE)) return null
        
        return EditorState(
            pinchScale = prefs.getFloat(KEY_PINCH_SCALE, 1f),
            panOffsetX = prefs.getFloat(KEY_PAN_OFFSET_X, 0f),
            panOffsetY = prefs.getFloat(KEY_PAN_OFFSET_Y, 0f),
            zoomOffset = prefs.getInt(KEY_SETTINGS_ZOOM_OFFSET, -2),
            brightness = prefs.getFloat(KEY_SETTINGS_BRIGHTNESS, 1f),
            blur = prefs.getFloat(KEY_SETTINGS_BLUR, 0f),
            overlayOpacity = prefs.getFloat(KEY_SETTINGS_OVERLAY_OPACITY, 0.1f)
        )
    }
    
    fun clearEditorState(context: Context) {
        getPrefs(context).edit().apply {
            remove(KEY_PINCH_SCALE)
            remove(KEY_PAN_OFFSET_X)
            remove(KEY_PAN_OFFSET_Y)
            remove(KEY_SETTINGS_ZOOM_OFFSET)
            remove(KEY_SETTINGS_BRIGHTNESS)
            remove(KEY_SETTINGS_BLUR)
            remove(KEY_SETTINGS_OVERLAY_OPACITY)
            apply()
        }
    }
    
    data class EditorState(
        val pinchScale: Float,
        val panOffsetX: Float,
        val panOffsetY: Float,
        val zoomOffset: Int,
        val brightness: Float,
        val blur: Float,
        val overlayOpacity: Float
    )
}

