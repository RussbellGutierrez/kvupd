package com.upd.kvupd.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun SupportMapFragment.awaitMap(): GoogleMap =
    suspendCancellableCoroutine { cont ->
        getMapAsync { map ->
            if (cont.isActive) cont.resume(map)
        }
    }

fun GoogleMap.settingsMap() {
    isTrafficEnabled = false
    setMaxZoomPreference(20f)
    setMinZoomPreference(10f)
    mapType = GoogleMap.MAP_TYPE_NORMAL
    uiSettings.isZoomControlsEnabled = false
    uiSettings.isZoomGesturesEnabled = true
    uiSettings.isRotateGesturesEnabled = false
    uiSettings.isMyLocationButtonEnabled = false
}