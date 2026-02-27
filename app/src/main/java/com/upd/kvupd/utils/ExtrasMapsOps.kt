package com.upd.kvupd.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.Pedimap
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

fun Pedimap.icono(context: Context): BitmapDescriptor {
    val drawableId = when (emitiendo) {
        1 -> R.drawable.pin_emite
        else -> R.drawable.pin_noemite
    }
    return context.vectorToBitmapDescriptor(drawableId)
}

fun FlowCliente.icono(context: Context): BitmapDescriptor {
    val drawableId = when {
        baja == 1 -> R.drawable.pin_otros
        compras == 1 -> R.drawable.pin_peligro
        ventas == 0 -> R.drawable.pin_venta
        else -> R.drawable.pin_chess
    }
    return context.vectorToBitmapDescriptor(drawableId)
}

fun FlowBajaSupervisor.icono(context: Context): BitmapDescriptor {
    val drawableId = R.drawable.pin_bajas
    return context.vectorToBitmapDescriptor(drawableId)
}

// Utilidad general para convertir un vector a BitmapDescriptor
fun Context.vectorToBitmapDescriptor(drawableId: Int): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(this, drawableId)
        ?: return BitmapDescriptorFactory.defaultMarker()
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}