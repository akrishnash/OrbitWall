# Regions Data Structure

This document explains how to expand the wallpapers collection to ~1000 regions.

## Category System

The app uses a multi-tag system where each region can belong to multiple categories:

### Continent Categories
- `africa` - Africa
- `asia` - Asia  
- `europe` - Europe
- `north-america` - North America
- `south-america` - South America
- `oceania` - Oceania (Australia, Pacific Islands)
- `antarctica` - Antarctica

### Feature Categories
- `landmarks` - Famous structures, monuments, historical sites
- `beaches` - Coastal areas, tropical paradises
- `mountains` - Mountain peaks and ranges
- `volcanoes` - Active and dormant volcanoes
- `scenic` - Beautiful natural landscapes
- `islands` - Islands, atolls, archipelagos

### Other Categories
- `urban` - Cities, towns, built environments
- `nature` - Forests, lakes, natural wonders
- `desert` - Deserts, dunes, arid regions
- `ocean` - Coastal views, reefs, marine features
- `ice` - Glaciers, polar regions, frozen landscapes

## Region Tag Examples

A region should have tags like:
```kotlin
Region(
    id = "1",
    name = "Mount Kilimanjaro, Tanzania",
    location = GeoLocation(-3.0674, 37.3556),
    zoom = 13,
    tags = listOf("africa", "mountains", "volcanoes", "scenic")
)
```

A beach in Asia:
```kotlin
tags = listOf("asia", "beaches", "islands", "scenic")
```

A landmark in Europe:
```kotlin
tags = listOf("europe", "landmarks", "urban", "history")
```

## Current Implementation

The regions are defined in `app/src/main/java/com/orbitwall/model/Models.kt` in the `PredefinedRegions` list.

## Expansion Strategy

To reach ~1000 regions, you can:

1. **Add by Continent**: 
   - ~150 regions per major continent (Africa, Asia, Europe, North America, South America)
   - ~50 regions for Oceania
   - ~20 regions for Antarctica

2. **Add by Category**:
   - ~100 landmarks (worldwide)
   - ~150 beaches (tropical and temperate)
   - ~150 mountains (all continents)
   - ~100 volcanoes (active and dormant)
   - ~200 scenic locations (diverse landscapes)
   - ~100 islands (various types)
   - Remaining spread across other categories

3. **Use a Data Source**:
   - World Heritage Sites (~1000 locations)
   - National Parks worldwide
   - Famous landmarks databases
   - Geographic feature databases

## Format for New Regions

```kotlin
Region(
    id = "{unique_id}",
    name = "{Location Name}, {Country/Region}",
    location = GeoLocation(latitude, longitude),
    zoom = {appropriate_zoom_level},  // Typically 11-16, higher for detailed features
    tags = listOf("continent", "feature_type", "other_tags")
)
```

## Zoom Level Guidelines

- **Country/Region Overview**: 10-11
- **Large Natural Features**: 11-12
- **Cities/Towns**: 13-14
- **Specific Landmarks**: 14-16
- **Small Features**: 15-16

## Testing After Expansion

After adding regions:
1. Test app performance with large dataset
2. Verify filtering by category works correctly
3. Check image loading performance (should still be optimized)
4. Test search/filter functionality

