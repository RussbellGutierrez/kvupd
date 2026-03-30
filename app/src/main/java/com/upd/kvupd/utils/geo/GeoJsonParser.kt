package com.upd.kvupd.utils.geo

import com.google.android.gms.maps.model.LatLng
import com.upd.kvupd.data.model.DistritoPolygon
import org.json.JSONArray
import org.json.JSONObject

object GeoJsonParser {

    fun parseDistritos(json: String): List<DistritoPolygon> {
        val list = mutableListOf<DistritoPolygon>()
        val root = JSONObject(json)
        val features = root.getJSONArray("features")

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)

            val properties = feature.getJSONObject("properties")
            val nombre = properties.getString("nombre")
            val codigo = properties.getString("codigo")

            val geometry = feature.getJSONObject("geometry")
            val type = geometry.getString("type")
            val coords = geometry.getJSONArray("coordinates")

            val polygons = mutableListOf<List<LatLng>>()

            if (type == "Polygon") {
                polygons.add(parsePolygon(coords))
            } else if (type == "MultiPolygon") {
                for (j in 0 until coords.length()) {
                    polygons.add(parsePolygon(coords.getJSONArray(j)))
                }
            }

            list.add(DistritoPolygon(codigo, nombre, polygons))
        }

        return list
    }

    private fun parsePolygon(array: JSONArray): List<LatLng> {
        val list = mutableListOf<LatLng>()
        val ring = array.getJSONArray(0)

        for (i in 0 until ring.length()) {
            val point = ring.getJSONArray(i)
            val lng = point.getDouble(0)
            val lat = point.getDouble(1)
            list.add(LatLng(lat, lng))
        }

        return list
    }
}