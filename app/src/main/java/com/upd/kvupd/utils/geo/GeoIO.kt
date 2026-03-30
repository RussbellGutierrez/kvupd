package com.upd.kvupd.utils.geo

import android.content.Context

fun Context.loadGeoJson(fileName: String): String {
    return assets.open(fileName).bufferedReader().use { it.readText() }
}