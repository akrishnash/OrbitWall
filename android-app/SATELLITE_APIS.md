# Satellite Image APIs Guide

## Current Implementation

**ESRI ArcGIS World Imagery**
- **URL**: `https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}`
- **Type**: Tile-based (256x256 pixels per tile)
- **Cost**: Free (with usage limits)
- **Quality**: Good
- **Pros**: Free, no API key needed, good coverage
- **Cons**: Single tiles are small, need stitching for larger images

## Alternative Satellite APIs

### 1. Mapbox Satellite (Recommended for Better Quality)

**Setup:**
1. Sign up at https://www.mapbox.com
2. Get API key
3. Add to `local.properties`: `MAPBOX_API_KEY=your_key_here`

**URL Format:**
```
https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}@2x.png?access_token={token}
```

**Pros:**
- Higher resolution tiles (512x512 with @2x)
- Better image quality
- Good API documentation
- Free tier: 50,000 requests/month

**Cons:**
- Requires API key
- Usage limits on free tier

### 2. Google Maps Satellite

**Setup:**
1. Get API key from Google Cloud Console
2. Enable Maps JavaScript API and Static Maps API
3. Add to `local.properties`: `GOOGLE_MAPS_API_KEY=your_key_here`

**URL Format:**
```
https://maps.googleapis.com/maps/api/staticmap?center={lat},{lon}&zoom={z}&size=640x640&maptype=satellite&key={key}
```

**Pros:**
- Excellent quality
- Direct image export (no stitching needed)
- Good coverage

**Cons:**
- Requires API key
- Paid service (though has free tier)
- Rate limits

### 3. Bing Maps Satellite

**Setup:**
1. Get API key from Bing Maps Portal
2. Add to `local.properties`: `BING_MAPS_API_KEY=your_key_here`

**URL Format:**
```
https://t0.tiles.virtualearth.net/tiles/a{quadkey}.jpeg?g=1&key={key}
```

**Pros:**
- Good quality
- Free tier available

**Cons:**
- Requires API key
- More complex quadkey system

### 4. NASA Worldview

**URL Format:**
```
https://worldview.earthdata.nasa.gov/wmts/epsg3857/best/BlueMarble_ShadedRelief_Bathymetry/default/{date}/GoogleMapsCompatible_Level8/{z}/{y}/{x}.jpg
```

**Pros:**
- Free, no API key
- High quality scientific imagery
- Different satellite sources

**Cons:**
- Different imagery (more scientific/atmospheric)
- May not have same aesthetic

### 5. Sentinel Hub (Best Quality, Requires Setup)

**Setup:**
1. Sign up at https://www.sentinel-hub.com
2. Get OAuth credentials
3. Configure instance

**Pros:**
- Best satellite imagery quality
- Multiple satellite sources (Sentinel-2, Landsat, etc.)
- Custom processing

**Cons:**
- Complex setup
- Requires authentication
- Paid for commercial use

## Implementation Strategy

### For Gallery Thumbnails (Current Optimization)
- **Use**: Optimized ESRI tiles at zoom 9 (good balance)
- **Size**: Single tile (256x256) → Coil resizes to card size
- **Performance**: Fast loading, small file size (~20-50KB per image)

### For Full Wallpaper Generation (Editor Screen)
- **Use**: ESRI tiles stitched together (already implemented in WallpaperGenerator)
- **Zoom**: Based on user selection (zoom 12-16)
- **Quality**: Multiple tiles stitched = high quality final image

### Future Enhancement Options

1. **Add Mapbox Support** (if API key available)
   ```kotlin
   const val MapboxTileTemplate = 
       "https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}@2x.png?access_token={token}"
   ```

2. **Implement Image Caching Service**
   - Cache thumbnails locally
   - Pre-generate common thumbnails
   - Use Coil's built-in caching (already enabled)

3. **Thumbnail Pre-generation**
   - Generate composite thumbnails server-side
   - Store optimized previews
   - Faster loading for gallery

## Current Optimization (Implemented)

✅ **Optimized thumbnail zoom level** (zoom 9 instead of variable)
✅ **Coil image resizing** (loads only needed size)
✅ **Image caching** (memory + disk cache enabled)
✅ **Single tile per thumbnail** (fastest loading)

## Performance Metrics

**Before optimization:**
- Thumbnail size: ~100-200KB (high zoom tiles)
- Load time: 2-5 seconds per image
- Memory usage: High (full-size images)

**After optimization:**
- Thumbnail size: ~20-50KB (optimized zoom)
- Load time: 0.5-1 second per image
- Memory usage: Low (resized to card dimensions)






