package com.orbitwall.wallpaper

import com.orbitwall.model.GeoLocation

object TileMath {
    fun latLonToTilePoint(lat: Double, lon: Double, zoom: Int): Pair<Double, Double> {
        val n = Math.pow(2.0, zoom.toDouble())
        val x = n * ((lon + 180.0) / 360.0)
        val latRad = Math.toRadians(lat)
        val y = n * (1 - (Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2.0
        return x to y
    }

    fun tileToLatLon(x: Double, y: Double, zoom: Int): GeoLocation {
        val n = Math.pow(2.0, zoom.toDouble())
        val lon = x / n * 360.0 - 180.0
        val latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)))
        val lat = Math.toDegrees(latRad)
        return GeoLocation(lat = lat, lon = lon)
    }

    fun metersPerPixel(lat: Double, zoom: Int): Double {
        return (156543.03392 * Math.cos(Math.toRadians(lat))) / Math.pow(2.0, zoom.toDouble())
    }
}
