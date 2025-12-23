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
     * Returns an optimized thumbnail URL for fast gallery loading.
     */
    fun getPreviewImageUrl(region: Region): String {
        // Use optimized thumbnail generator for better performance
        return ThumbnailGenerator.getOptimizedThumbnailUrl(region)
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
    // Continents
    GalleryCategory("africa", "Africa"),
    GalleryCategory("asia", "Asia"),
    GalleryCategory("europe", "Europe"),
    GalleryCategory("north-america", "North America"),
    GalleryCategory("south-america", "South America"),
    GalleryCategory("oceania", "Oceania"),
    GalleryCategory("antarctica", "Antarctica"),
    // Feature Types
    GalleryCategory("landmarks", "Landmarks"),
    GalleryCategory("beaches", "Beaches"),
    GalleryCategory("mountains", "Mountains"),
    GalleryCategory("volcanoes", "Volcanoes"),
    GalleryCategory("scenic", "Scenic"),
    GalleryCategory("islands", "Islands"),
    // Other Categories
    GalleryCategory("urban", "Urban"),
    GalleryCategory("nature", "Nature"),
    GalleryCategory("desert", "Desert"),
    GalleryCategory("ocean", "Ocean"),
    GalleryCategory("ice", "Ice")
)

val DefaultSettings = WallSettings()

val PredefinedRegions = listOf(
    // AFRICA (15 regions)
    Region(id = "1", name = "Richat Structure, Mauritania", location = GeoLocation(21.1269, -11.4016), zoom = 12, tags = listOf("africa", "landmarks", "geology", "desert", "scenic")),
    Region(id = "2", name = "Namib Desert, Namibia", location = GeoLocation(-24.7500, 15.2833), zoom = 12, tags = listOf("africa", "desert", "dunes", "scenic")),
    Region(id = "3", name = "Sahara Desert, Algeria", location = GeoLocation(25.0000, 0.0000), zoom = 12, tags = listOf("africa", "desert", "dunes", "scenic")),
    Region(id = "4", name = "Victoria Falls, Zambia/Zimbabwe", location = GeoLocation(-17.9243, 25.8572), zoom = 14, tags = listOf("africa", "landmarks", "scenic", "nature")),
    Region(id = "5", name = "Mount Kilimanjaro, Tanzania", location = GeoLocation(-3.0674, 37.3556), zoom = 13, tags = listOf("africa", "mountains", "volcanoes", "scenic")),
    Region(id = "6", name = "Ngorongoro Crater, Tanzania", location = GeoLocation(-3.1794, 35.5431), zoom = 14, tags = listOf("africa", "landmarks", "scenic", "nature")),
    Region(id = "7", name = "Great Pyramid of Giza, Egypt", location = GeoLocation(29.9792, 31.1342), zoom = 16, tags = listOf("africa", "landmarks", "urban", "history")),
    Region(id = "8", name = "Suez Canal, Egypt", location = GeoLocation(30.5852, 32.2654), zoom = 13, tags = listOf("africa", "landmarks", "ocean", "urban")),
    Region(id = "9", name = "Cape Town, South Africa", location = GeoLocation(-33.9249, 18.4241), zoom = 13, tags = listOf("africa", "urban", "mountains", "ocean")),
    Region(id = "10", name = "Kruger National Park, South Africa", location = GeoLocation(-23.9884, 31.5547), zoom = 12, tags = listOf("africa", "nature", "scenic")),
    Region(id = "11", name = "Serengeti National Park, Tanzania", location = GeoLocation(-2.1540, 34.6857), zoom = 12, tags = listOf("africa", "nature", "scenic")),
    Region(id = "12", name = "Okavango Delta, Botswana", location = GeoLocation(-19.2667, 22.7667), zoom = 12, tags = listOf("africa", "nature", "ocean", "scenic")),
    Region(id = "13", name = "Mount Kenya, Kenya", location = GeoLocation(-0.1520, 37.3084), zoom = 13, tags = listOf("africa", "mountains", "scenic")),
    Region(id = "14", name = "Drakensberg Mountains, South Africa", location = GeoLocation(-29.3833, 29.2667), zoom = 12, tags = listOf("africa", "mountains", "scenic")),
    Region(id = "15", name = "Lake Malawi, Malawi", location = GeoLocation(-12.0000, 34.0000), zoom = 11, tags = listOf("africa", "nature", "ocean", "scenic")),
    
    // ASIA (25 regions)
    Region(id = "16", name = "Palm Jumeirah, Dubai", location = GeoLocation(25.1124, 55.1390), zoom = 14, tags = listOf("asia", "landmarks", "islands", "urban", "beaches")),
    Region(id = "17", name = "Mount Fuji, Japan", location = GeoLocation(35.3606, 138.7274), zoom = 13, tags = listOf("asia", "mountains", "volcanoes", "landmarks", "scenic")),
    Region(id = "18", name = "Mount Everest, Nepal", location = GeoLocation(27.9881, 86.9250), zoom = 13, tags = listOf("asia", "mountains", "landmarks", "scenic")),
    Region(id = "19", name = "Maldives Atolls", location = GeoLocation(3.2028, 73.2207), zoom = 15, tags = listOf("asia", "beaches", "islands", "ocean", "scenic")),
    Region(id = "20", name = "Tokyo Bay, Japan", location = GeoLocation(35.6762, 139.6503), zoom = 13, tags = listOf("asia", "urban", "city", "ocean")),
    Region(id = "21", name = "Bali Rice Terraces, Indonesia", location = GeoLocation(-8.4095, 115.1889), zoom = 15, tags = listOf("asia", "nature", "scenic", "agriculture")),
    Region(id = "22", name = "Lake Baikal, Russia", location = GeoLocation(53.5000, 108.0000), zoom = 11, tags = listOf("asia", "nature", "ice", "lake", "scenic")),
    Region(id = "23", name = "Angkor Wat, Cambodia", location = GeoLocation(13.4125, 103.8670), zoom = 14, tags = listOf("asia", "landmarks", "history", "scenic")),
    Region(id = "24", name = "Socotra Island, Yemen", location = GeoLocation(12.4634, 53.8230), zoom = 13, tags = listOf("asia", "islands", "nature", "scenic")),
    Region(id = "25", name = "Abu Dhabi, UAE", location = GeoLocation(24.4539, 54.3773), zoom = 13, tags = listOf("asia", "urban", "city", "desert")),
    Region(id = "26", name = "Himalayas, Nepal", location = GeoLocation(28.0000, 84.0000), zoom = 11, tags = listOf("asia", "mountains", "scenic")),
    Region(id = "27", name = "Petra, Jordan", location = GeoLocation(30.3285, 35.4444), zoom = 15, tags = listOf("asia", "landmarks", "history", "desert")),
    Region(id = "28", name = "Great Wall of China", location = GeoLocation(40.4319, 116.5704), zoom = 13, tags = listOf("asia", "landmarks", "history", "mountains")),
    Region(id = "29", name = "Taj Mahal, India", location = GeoLocation(27.1751, 78.0421), zoom = 16, tags = listOf("asia", "landmarks", "history", "urban")),
    Region(id = "30", name = "Bora Bora, French Polynesia", location = GeoLocation(-16.5004, -151.7415), zoom = 14, tags = listOf("oceania", "beaches", "islands", "ocean", "scenic")),
    Region(id = "31", name = "Mount Bromo, Indonesia", location = GeoLocation(-7.9425, 112.9530), zoom = 14, tags = listOf("asia", "volcanoes", "mountains", "scenic")),
    Region(id = "32", name = "Ha Long Bay, Vietnam", location = GeoLocation(20.9101, 107.1839), zoom = 13, tags = listOf("asia", "ocean", "islands", "scenic")),
    Region(id = "33", name = "Zhangjiajie, China", location = GeoLocation(29.1274, 110.4792), zoom = 13, tags = listOf("asia", "mountains", "scenic", "nature")),
    Region(id = "34", name = "Dead Sea, Jordan/Israel", location = GeoLocation(31.5000, 35.5000), zoom = 12, tags = listOf("asia", "nature", "desert", "scenic")),
    Region(id = "35", name = "Mount Kinabalu, Malaysia", location = GeoLocation(6.0750, 116.5581), zoom = 14, tags = listOf("asia", "mountains", "scenic")),
    Region(id = "36", name = "Pamukkale, Turkey", location = GeoLocation(37.9236, 29.1173), zoom = 14, tags = listOf("asia", "landmarks", "geology", "scenic")),
    Region(id = "37", name = "Mount Ararat, Turkey", location = GeoLocation(39.7019, 44.2983), zoom = 13, tags = listOf("asia", "mountains", "volcanoes", "scenic")),
    Region(id = "38", name = "Jeju Island, South Korea", location = GeoLocation(33.4996, 126.5312), zoom = 13, tags = listOf("asia", "islands", "volcanoes", "scenic")),
    Region(id = "39", name = "Gobi Desert, Mongolia", location = GeoLocation(42.5000, 103.0000), zoom = 11, tags = listOf("asia", "desert", "scenic")),
    Region(id = "40", name = "Mount K2, Pakistan", location = GeoLocation(35.8806, 76.5133), zoom = 13, tags = listOf("asia", "mountains", "scenic")),
    
    // EUROPE (20 regions)
    Region(id = "41", name = "Venice, Italy", location = GeoLocation(45.4408, 12.3155), zoom = 14, tags = listOf("europe", "urban", "city", "ocean")),
    Region(id = "42", name = "Eyjafjallajökull, Iceland", location = GeoLocation(63.6294, -19.6228), zoom = 13, tags = listOf("europe", "volcanoes", "ice", "scenic", "geology")),
    Region(id = "43", name = "Santorini, Greece", location = GeoLocation(36.3932, 25.4615), zoom = 14, tags = listOf("europe", "islands", "beaches", "volcanoes", "scenic", "urban")),
    Region(id = "44", name = "Norwegian Fjords", location = GeoLocation(60.4720, 5.3108), zoom = 12, tags = listOf("europe", "ocean", "nature", "ice", "mountains", "scenic")),
    Region(id = "45", name = "Matterhorn, Switzerland", location = GeoLocation(45.9763, 7.6586), zoom = 14, tags = listOf("europe", "mountains", "scenic")),
    Region(id = "46", name = "Aurora Borealis Region, Norway", location = GeoLocation(69.6492, 18.9553), zoom = 10, tags = listOf("europe", "ice", "nature", "scenic")),
    Region(id = "47", name = "Eiffel Tower, Paris", location = GeoLocation(48.8584, 2.2945), zoom = 16, tags = listOf("europe", "landmarks", "urban", "history")),
    Region(id = "48", name = "Sagrada Familia, Barcelona", location = GeoLocation(41.4036, 2.1744), zoom = 16, tags = listOf("europe", "landmarks", "urban", "history")),
    Region(id = "49", name = "Plitvice Lakes, Croatia", location = GeoLocation(44.8654, 15.5820), zoom = 14, tags = listOf("europe", "nature", "scenic")),
    Region(id = "50", name = "Swiss Alps", location = GeoLocation(46.5197, 9.8385), zoom = 12, tags = listOf("europe", "mountains", "ice", "scenic")),
    Region(id = "51", name = "Sicily, Italy", location = GeoLocation(37.5665, 14.2681), zoom = 12, tags = listOf("europe", "islands", "volcanoes", "scenic")),
    Region(id = "52", name = "Lofoten Islands, Norway", location = GeoLocation(68.2385, 13.6167), zoom = 12, tags = listOf("europe", "islands", "mountains", "ocean", "scenic")),
    Region(id = "53", name = "Mont Saint-Michel, France", location = GeoLocation(48.6360, -1.5115), zoom = 15, tags = listOf("europe", "landmarks", "ocean", "history")),
    Region(id = "54", name = "Lake Como, Italy", location = GeoLocation(46.0000, 9.2667), zoom = 13, tags = listOf("europe", "nature", "mountains", "scenic")),
    Region(id = "55", name = "Dolomites, Italy", location = GeoLocation(46.5000, 11.5000), zoom = 12, tags = listOf("europe", "mountains", "scenic")),
    Region(id = "56", name = "Vatnajökull Glacier, Iceland", location = GeoLocation(64.4219, -16.7894), zoom = 12, tags = listOf("europe", "ice", "nature", "scenic")),
    Region(id = "57", name = "Scottish Highlands", location = GeoLocation(57.0000, -4.0000), zoom = 11, tags = listOf("europe", "mountains", "nature", "scenic")),
    Region(id = "58", name = "Amalfi Coast, Italy", location = GeoLocation(40.6340, 14.6027), zoom = 13, tags = listOf("europe", "beaches", "ocean", "scenic", "urban")),
    Region(id = "59", name = "Mykonos, Greece", location = GeoLocation(37.4467, 25.3289), zoom = 14, tags = listOf("europe", "islands", "beaches", "scenic", "urban")),
    Region(id = "60", name = "Cinque Terre, Italy", location = GeoLocation(44.1270, 9.7144), zoom = 14, tags = listOf("europe", "beaches", "ocean", "scenic", "urban")),
    
    // NORTH AMERICA (20 regions)
    Region(id = "61", name = "Grand Prismatic Spring, USA", location = GeoLocation(44.5250, -110.8382), zoom = 16, tags = listOf("north-america", "nature", "geology", "scenic")),
    Region(id = "62", name = "Central Park, NYC", location = GeoLocation(40.7829, -73.9654), zoom = 14, tags = listOf("north-america", "urban", "city")),
    Region(id = "63", name = "Hawaii Volcano, USA", location = GeoLocation(19.4069, -155.2834), zoom = 14, tags = listOf("north-america", "volcanoes", "islands", "ocean", "scenic")),
    Region(id = "64", name = "Grand Canyon, USA", location = GeoLocation(36.1069, -112.1129), zoom = 13, tags = listOf("north-america", "geology", "nature", "scenic")),
    Region(id = "65", name = "Mammoth Hot Springs, Yellowstone, USA", location = GeoLocation(44.9699, -110.7006), zoom = 14, tags = listOf("north-america", "nature", "geology", "scenic")),
    Region(id = "66", name = "Niagara Falls, USA/Canada", location = GeoLocation(43.0962, -79.0377), zoom = 15, tags = listOf("north-america", "landmarks", "nature", "scenic")),
    Region(id = "67", name = "Yosemite Valley, USA", location = GeoLocation(37.8651, -119.5383), zoom = 13, tags = listOf("north-america", "mountains", "nature", "scenic")),
    Region(id = "68", name = "Denali, Alaska", location = GeoLocation(63.0695, -151.0070), zoom = 12, tags = listOf("north-america", "mountains", "ice", "scenic")),
    Region(id = "69", name = "Antelope Canyon, USA", location = GeoLocation(36.8619, -111.3743), zoom = 15, tags = listOf("north-america", "geology", "desert", "scenic")),
    Region(id = "70", name = "Monument Valley, USA", location = GeoLocation(36.9989, -110.1124), zoom = 13, tags = listOf("north-america", "desert", "geology", "scenic")),
    Region(id = "71", name = "Banff National Park, Canada", location = GeoLocation(51.4968, -115.9281), zoom = 12, tags = listOf("north-america", "mountains", "ice", "nature", "scenic")),
    Region(id = "72", name = "Hawaii Beaches", location = GeoLocation(21.3099, -157.8581), zoom = 14, tags = listOf("north-america", "beaches", "islands", "ocean", "scenic")),
    Region(id = "73", name = "Mount Rainier, USA", location = GeoLocation(46.8523, -121.7603), zoom = 13, tags = listOf("north-america", "mountains", "volcanoes", "scenic")),
    Region(id = "74", name = "Zion National Park, USA", location = GeoLocation(37.2982, -113.0263), zoom = 13, tags = listOf("north-america", "mountains", "geology", "scenic")),
    Region(id = "75", name = "Arches National Park, USA", location = GeoLocation(38.7331, -109.5925), zoom = 13, tags = listOf("north-america", "geology", "desert", "scenic")),
    Region(id = "76", name = "Lake Tahoe, USA", location = GeoLocation(39.0968, -120.0324), zoom = 13, tags = listOf("north-america", "nature", "mountains", "scenic")),
    Region(id = "77", name = "Glacier National Park, USA", location = GeoLocation(48.7596, -113.7870), zoom = 12, tags = listOf("north-america", "mountains", "ice", "nature", "scenic")),
    Region(id = "78", name = "Bryce Canyon, USA", location = GeoLocation(37.5934, -112.1871), zoom = 13, tags = listOf("north-america", "geology", "desert", "scenic")),
    Region(id = "79", name = "Maui, Hawaii", location = GeoLocation(20.7984, -156.3319), zoom = 13, tags = listOf("north-america", "islands", "beaches", "volcanoes", "scenic")),
    Region(id = "80", name = "Jasper National Park, Canada", location = GeoLocation(52.8733, -118.0814), zoom = 12, tags = listOf("north-america", "mountains", "ice", "nature", "scenic")),
    
    // SOUTH AMERICA (15 regions)
    Region(id = "81", name = "Salar de Uyuni, Bolivia", location = GeoLocation(-20.1337, -67.4891), zoom = 13, tags = listOf("south-america", "desert", "scenic")),
    Region(id = "82", name = "Patagonia Mountains", location = GeoLocation(-50.4833, -73.0167), zoom = 12, tags = listOf("south-america", "mountains", "ice", "scenic")),
    Region(id = "83", name = "Amazon Rainforest", location = GeoLocation(-3.4653, -62.2159), zoom = 11, tags = listOf("south-america", "nature", "scenic")),
    Region(id = "84", name = "Machu Picchu, Peru", location = GeoLocation(-13.1631, -72.5450), zoom = 15, tags = listOf("south-america", "landmarks", "mountains", "scenic", "history")),
    Region(id = "85", name = "Iguazu Falls, Argentina/Brazil", location = GeoLocation(-25.6953, -54.4367), zoom = 14, tags = listOf("south-america", "landmarks", "nature", "scenic")),
    Region(id = "86", name = "Atacama Desert, Chile", location = GeoLocation(-24.5000, -69.2500), zoom = 12, tags = listOf("south-america", "desert", "scenic")),
    Region(id = "87", name = "Torres del Paine, Chile", location = GeoLocation(-50.9423, -73.4068), zoom = 13, tags = listOf("south-america", "mountains", "ice", "scenic")),
    Region(id = "88", name = "Angel Falls, Venezuela", location = GeoLocation(5.9700, -62.5361), zoom = 14, tags = listOf("south-america", "landmarks", "nature", "scenic")),
    Region(id = "89", name = "Easter Island, Chile", location = GeoLocation(-27.1127, -109.3497), zoom = 13, tags = listOf("oceania", "islands", "landmarks", "history", "scenic")),
    Region(id = "90", name = "Galapagos Islands, Ecuador", location = GeoLocation(-0.7893, -91.0543), zoom = 13, tags = listOf("south-america", "islands", "nature", "ocean", "scenic")),
    Region(id = "91", name = "Fitz Roy, Argentina", location = GeoLocation(-49.2713, -73.0533), zoom = 13, tags = listOf("south-america", "mountains", "scenic")),
    Region(id = "92", name = "Mount Aconcagua, Argentina", location = GeoLocation(-32.6532, -70.0109), zoom = 13, tags = listOf("south-america", "mountains", "scenic")),
    Region(id = "93", name = "Rio de Janeiro, Brazil", location = GeoLocation(-22.9068, -43.1729), zoom = 13, tags = listOf("south-america", "urban", "city", "beaches", "ocean")),
    Region(id = "94", name = "Nazca Lines, Peru", location = GeoLocation(-14.7167, -75.1333), zoom = 14, tags = listOf("south-america", "landmarks", "history", "desert")),
    Region(id = "95", name = "Lake Titicaca, Peru/Bolivia", location = GeoLocation(-15.9254, -69.3354), zoom = 12, tags = listOf("south-america", "nature", "scenic")),
    
    // OCEANIA (10 regions)
    Region(id = "96", name = "Great Barrier Reef, Australia", location = GeoLocation(-18.2871, 147.6992), zoom = 15, tags = listOf("oceania", "ocean", "nature", "scenic")),
    Region(id = "97", name = "Uluru, Australia", location = GeoLocation(-25.3444, 131.0369), zoom = 13, tags = listOf("oceania", "landmarks", "desert", "scenic")),
    Region(id = "98", name = "Sydney Harbour, Australia", location = GeoLocation(-33.8568, 151.2153), zoom = 14, tags = listOf("oceania", "urban", "city", "ocean", "landmarks")),
    Region(id = "99", name = "Milford Sound, New Zealand", location = GeoLocation(-44.6414, 167.8972), zoom = 13, tags = listOf("oceania", "ocean", "mountains", "scenic")),
    Region(id = "100", name = "Mount Cook, New Zealand", location = GeoLocation(-43.5944, 170.1417), zoom = 13, tags = listOf("oceania", "mountains", "ice", "scenic")),
    Region(id = "101", name = "Fiji Islands", location = GeoLocation(-17.7134, 178.0650), zoom = 13, tags = listOf("oceania", "islands", "beaches", "ocean", "scenic")),
    Region(id = "102", name = "Tahiti, French Polynesia", location = GeoLocation(-17.6509, -149.4260), zoom = 13, tags = listOf("oceania", "islands", "beaches", "volcanoes", "scenic")),
    Region(id = "103", name = "Great Ocean Road, Australia", location = GeoLocation(-38.6806, 143.3906), zoom = 13, tags = listOf("oceania", "beaches", "ocean", "scenic")),
    Region(id = "104", name = "Tongariro National Park, New Zealand", location = GeoLocation(-39.2927, 175.5622), zoom = 13, tags = listOf("oceania", "volcanoes", "mountains", "scenic")),
    Region(id = "105", name = "Whitsunday Islands, Australia", location = GeoLocation(-20.2753, 149.0450), zoom = 14, tags = listOf("oceania", "islands", "beaches", "ocean", "scenic")),
    
    // ANTARCTICA (5 regions)
    Region(id = "106", name = "Antarctica Ice", location = GeoLocation(-75.0000, 0.0000), zoom = 11, tags = listOf("antarctica", "ice", "scenic")),
    Region(id = "107", name = "Mount Erebus, Antarctica", location = GeoLocation(-77.5283, 167.1550), zoom = 13, tags = listOf("antarctica", "volcanoes", "ice", "scenic")),
    Region(id = "108", name = "Ross Ice Shelf, Antarctica", location = GeoLocation(-81.5000, -175.0000), zoom = 11, tags = listOf("antarctica", "ice", "scenic")),
    Region(id = "109", name = "Antarctic Peninsula", location = GeoLocation(-64.8000, -63.0000), zoom = 11, tags = listOf("antarctica", "ice", "mountains", "scenic")),
    Region(id = "110", name = "Dry Valleys, Antarctica", location = GeoLocation(-77.5000, 162.0000), zoom = 12, tags = listOf("antarctica", "desert", "ice", "scenic"))
)

