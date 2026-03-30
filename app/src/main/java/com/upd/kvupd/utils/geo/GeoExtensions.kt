package com.upd.kvupd.utils.geo

import com.google.android.gms.maps.model.LatLng

fun LatLng.isInsidePolygon(polygon: List<LatLng>): Boolean {
    var intersectCount = 0

    for (i in polygon.indices) {
        val j = (i + 1) % polygon.size

        val xi = polygon[i].longitude
        val yi = polygon[i].latitude
        val xj = polygon[j].longitude
        val yj = polygon[j].latitude

        val intersect = ((yi > latitude) != (yj > latitude)) &&
                (longitude < (xj - xi) * (latitude - yi) / (yj - yi) + xi)

        if (intersect) intersectCount++
    }

    return intersectCount % 2 != 0
}