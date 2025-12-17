package com.orbitwall.model

import androidx.annotation.StringRes
import com.orbitwall.R

data class GeoLocation(
    val lat: Double,
    val lon: Double
)

data class Region(
    val id: String,
    val name: String,
    val location: GeoLocation,
    val zoom: Int,
    val tags: List<String>,
    val heroImage: String? = null
)

enum class Resolution(
    @StringRes val label: Int,
    val portraitWidth: Int,
    val landscapeWidth: Int
) {
    SCREEN(R.string.resolution_screen, 0, 0),
    RES_2K(R.string.resolution_2k, 1440, 2560),
    RES_4K(R.string.resolution_4k, 2160, 3840)
}

data class WallSettings(
    val blur: Float = 0f,
    val brightness: Float = 1f,
    val overlayOpacity: Float = 0.1f,
    val overlayColor: Long = 0xFF000000,
    val zoomOffset: Int = -2,
    val resolution: Resolution = Resolution.RES_2K
)

data class Dimensions(
    val width: Int,
    val height: Int
)

data class GalleryCategory(
    val id: String,
    val label: String,
    val regions: List<Region> = emptyList()
)

const val TileUrlTemplate =
    "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"

/**
 * Helper object for region-related utilities
 */
object RegionHelper {
    /**
     * Generate a preview thumbnail URL for a region using ESRI satellite tiles.
     * Returns a single center tile as a simple preview.
     */
    fun getPreviewImageUrl(region: Region): String {
        try {
            val previewZoom = (region.zoom - 2).coerceIn(3, 17) // Slightly zoomed out for better context
            val (tileX, tileY) = latLonToTilePoint(region.location.lat, region.location.lon, previewZoom)
            return TileUrlTemplate
                .replace("{z}", previewZoom.toString())
                .replace("{x}", tileX.toString())
                .replace("{y}", tileY.toString())
        } catch (e: Exception) {
            // Return a placeholder URL if calculation fails
            return "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/10/512/512"
        }
    }
}

/**
 * Extension function to generate a preview thumbnail URL for a region using ESRI satellite tiles.
 * Returns a single center tile as a simple preview.
 */
fun Region.getPreviewImageUrl(): String = RegionHelper.getPreviewImageUrl(this)

/**
 * Convert latitude/longitude to tile coordinates for a given zoom level
 */
internal fun latLonToTilePoint(lat: Double, lon: Double, zoom: Int): Pair<Int, Int> {
    val n = Math.pow(2.0, zoom.toDouble())
    val x = n * ((lon + 180.0) / 360.0)
    val latRad = Math.toRadians(lat)
    
    // Handle edge cases for latitude (avoid division by zero, etc.)
    val tanLat = Math.tan(latRad)
    val cosLat = Math.cos(latRad)
    val y = if (cosLat != 0.0) {
        n * (1 - (Math.log(tanLat + 1.0 / cosLat) / Math.PI)) / 2.0
    } else {
        // Fallback for poles
        if (lat > 0) 0.0 else n
    }
    
    // Normalize tile coordinates
    val normalizedX = ((x.toInt() % n.toInt()) + n.toInt()) % n.toInt()
    val normalizedY = y.toInt().coerceIn(0, (n.toInt() - 1))
    
    return normalizedX to normalizedY
}

val PredefinedCategories = listOf(
    GalleryCategory("all", "All"),
    GalleryCategory("urban", "Urban"),
    GalleryCategory("nature", "Nature"),
    GalleryCategory("ocean", "Ocean"),
    GalleryCategory("desert", "Desert"),
    GalleryCategory("mountain", "Mountain"),
    GalleryCategory("ice", "Ice"),
    GalleryCategory("agriculture", "Agriculture"),
    GalleryCategory("geology", "Geology")
)

val DefaultSettings = WallSettings()

val PredefinedRegions = listOf(
    Region(
        id = "1",
        name = "Richat Structure",
        location = GeoLocation(21.1269, -11.4016),
        zoom = 12,
        tags = listOf("desert", "geology", "africa")
    ),
    Region(
        id = "2",
        name = "Palm Jumeirah",
        location = GeoLocation(25.1124, 55.1390),
        zoom = 14,
        tags = listOf("urban", "island", "middle-east")
    ),
    Region(
        id = "3",
        name = "Grand Prismatic Spring",
        location = GeoLocation(44.5250, -110.8382),
        zoom = 16,
        tags = listOf("nature", "colorful", "usa")
    ),
    Region(
        id = "4",
        name = "Mount Fuji",
        location = GeoLocation(35.3606, 138.7274),
        zoom = 13,
        tags = listOf("mountain", "snow", "asia")
    ),
    Region(
        id = "5",
        name = "Great Barrier Reef",
        location = GeoLocation(-18.2871, 147.6992),
        zoom = 15,
        tags = listOf("ocean", "nature", "australia")
    ),
    Region(
        id = "6",
        name = "Central Park, NYC",
        location = GeoLocation(40.7829, -73.9654),
        zoom = 14,
        tags = listOf("urban", "city", "usa")
    ),
    Region(
        id = "7",
        name = "Namib Desert",
        location = GeoLocation(-24.7500, 15.2833),
        zoom = 12,
        tags = listOf("desert", "africa", "dunes")
    ),
    Region(
        id = "8",
        name = "Mount Everest",
        location = GeoLocation(27.9881, 86.9250),
        zoom = 13,
        tags = listOf("mountain", "snow", "asia", "himalayas")
    ),
    Region(
        id = "9",
        name = "Salar de Uyuni",
        location = GeoLocation(-20.1337, -67.4891),
        zoom = 13,
        tags = listOf("desert", "salt", "south-america")
    ),
    Region(
        id = "10",
        name = "Venice, Italy",
        location = GeoLocation(45.4408, 12.3155),
        zoom = 14,
        tags = listOf("urban", "water", "europe", "city")
    ),
    Region(
        id = "11",
        name = "Iceland Volcanoes",
        location = GeoLocation(64.9631, -19.0208),
        zoom = 13,
        tags = listOf("geology", "ice", "volcano", "europe")
    ),
    Region(
        id = "12",
        name = "Hawaii Volcano",
        location = GeoLocation(19.4069, -155.2834),
        zoom = 14,
        tags = listOf("nature", "volcano", "ocean", "usa")
    ),
    Region(
        id = "13",
        name = "Sahara Desert Dunes",
        location = GeoLocation(25.0000, 0.0000),
        zoom = 12,
        tags = listOf("desert", "africa", "dunes")
    ),
    Region(
        id = "14",
        name = "Patagonia Mountains",
        location = GeoLocation(-50.4833, -73.0167),
        zoom = 12,
        tags = listOf("mountain", "ice", "south-america")
    ),
    Region(
        id = "15",
        name = "Maldives Atolls",
        location = GeoLocation(3.2028, 73.2207),
        zoom = 15,
        tags = listOf("ocean", "island", "tropical", "asia")
    ),
    Region(
        id = "16",
        name = "Antarctica Ice",
        location = GeoLocation(-75.0000, 0.0000),
        zoom = 11,
        tags = listOf("ice", "antarctica", "glacier")
    ),
    Region(
        id = "17",
        name = "Amazon Rainforest",
        location = GeoLocation(-3.4653, -62.2159),
        zoom = 11,
        tags = listOf("nature", "forest", "south-america")
    ),
    Region(
        id = "18",
        name = "Dubai City",
        location = GeoLocation(25.2048, 55.2708),
        zoom = 13,
        tags = listOf("urban", "city", "middle-east", "desert")
    ),
    Region(
        id = "19",
        name = "Grand Canyon",
        location = GeoLocation(36.1069, -112.1129),
        zoom = 13,
        tags = listOf("geology", "nature", "usa", "canyon")
    ),
    Region(
        id = "20",
        name = "Tokyo Bay",
        location = GeoLocation(35.6762, 139.6503),
        zoom = 13,
        tags = listOf("urban", "city", "ocean", "asia")
    ),
    Region(
        id = "21",
        name = "Machu Picchu",
        location = GeoLocation(-13.1631, -72.5450),
        zoom = 15,
        tags = listOf("mountain", "history", "south-america", "nature")
    ),
    Region(
        id = "22",
        name = "Santorini, Greece",
        location = GeoLocation(36.3932, 25.4615),
        zoom = 14,
        tags = listOf("ocean", "urban", "europe", "island")
    ),
    Region(
        id = "23",
        name = "Bali Rice Terraces",
        location = GeoLocation(-8.4095, 115.1889),
        zoom = 15,
        tags = listOf("agriculture", "nature", "asia", "terrace")
    ),
    Region(
        id = "24",
        name = "Norwegian Fjords",
        location = GeoLocation(60.4720, 5.3108),
        zoom = 12,
        tags = listOf("ocean", "nature", "europe", "ice", "mountains")
    ),
    Region(
        id = "25",
        name = "Yellowstone Geysers",
        location = GeoLocation(44.4605, -110.8281),
        zoom = 14,
        tags = listOf("nature", "geology", "usa")
    ),
    Region(
        id = "26",
        name = "Matterhorn",
        location = GeoLocation(45.9763, 7.6586),
        zoom = 14,
        tags = listOf("mountain", "snow", "europe", "alps")
    ),
    Region(
        id = "27",
        name = "Lake Baikal",
        location = GeoLocation(53.5000, 108.0000),
        zoom = 11,
        tags = listOf("nature", "ice", "lake", "asia")
    ),
    Region(
        id = "28",
        name = "Angkor Wat",
        location = GeoLocation(13.4125, 103.8670),
        zoom = 14,
        tags = listOf("urban", "history", "nature", "asia")
    ),
    Region(
        id = "29",
        name = "Socotra Island",
        location = GeoLocation(12.4634, 53.8230),
        zoom = 13,
        tags = listOf("nature", "island", "unique", "middle-east")
    ),
    Region(
        id = "30",
        name = "Aurora Borealis Region",
        location = GeoLocation(69.6492, 18.9553),
        zoom = 10,
        tags = listOf("ice", "nature", "europe", "northern-lights")
    )
)

