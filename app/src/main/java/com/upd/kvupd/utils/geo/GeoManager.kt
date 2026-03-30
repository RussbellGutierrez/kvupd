package com.upd.kvupd.utils.geo

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.upd.kvupd.data.model.DistritoPolygon

object GeoManager {

    private var distritos: List<DistritoPolygon> = emptyList()
    private var loaded = false

    fun load(context: Context) {
        if (loaded) return

        val json = context.loadGeoJson("distritosPolygon.json")
        distritos = GeoJsonParser.parseDistritos(json)

        loaded = true
    }

    fun isLoaded() = loaded

    fun findDistrito(point: LatLng): String? {
        for (d in distritos) {
            for (poly in d.polygons) {
                if (point.isInsidePolygon(poly)) {
                    return d.codigo
                }
            }
        }
        return null
    }
}