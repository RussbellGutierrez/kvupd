package com.upd.kvupd.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.text.Editable
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.upd.kvupd.R
import com.upd.kvupd.data.model.MarkerMap
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.ui.dialog.DBuscar
import com.upd.kvupd.ui.dialog.DFiltroObs
import com.upd.kvupd.ui.dialog.DProgress
import com.upd.kvupd.utils.Constant.DL_WIDTH
import com.upd.kvupd.utils.Constant.D_HEIGHT
import com.upd.kvupd.utils.Constant.D_WIDTH
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun JSONObject.toReqBody(): RequestBody =
    RequestBody.create(MediaType.parse("application/json"), this.toString())

fun Editable.validateImei() = this.toString().length == 15

fun Fragment.toast(text: String, duration: Int = 0) {
    Toast.makeText(this.requireContext(), text, duration).show()
}

fun Context.toast(text: String, duration: Int = 0) {
    Toast.makeText(this, text, duration).show()
}

fun DialogFragment.setCreate() {
    this.dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.parentFragmentManager
}

fun DialogFragment.setResume(short: Boolean = true) {
    if (short) {
        this.dialog?.window?.setLayout(D_WIDTH, D_HEIGHT)
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
    } else {
        this.dialog?.window?.setLayout(DL_WIDTH, D_HEIGHT)
    }
}

fun Fragment.snack(text: String) {
    val view = this.requireActivity().window.decorView.findViewById<View>(android.R.id.content)
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
}

fun Activity.snack(text: String) {
    val view = this.window.decorView.findViewById<View>(android.R.id.content)
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
}

fun Context.isGPSDisabled(): Boolean {
    val locationManager =
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return (!gps && !network)
}

fun Date.dateToday(formato: Int) =
    this.timeToText(formato)

fun Fragment.search(list: MutableList<String>) {
    val bundle = bundleOf(
        "lista" to list
    )
    val dlg = DBuscar()
    dlg.arguments = bundle
    dlg.show(parentFragmentManager, "dialog")
}

fun Fragment.filterObs() {
    val dlg = DFiltroObs()
    dlg.show(parentFragmentManager, "dialog")
}

fun percent(dividendo: Double, divisor: Double): String {
    var result = if (divisor == 0.0) {
        "0.00"
    } else {
        val porcentaje = (dividendo * 100) / divisor
        "%.2f".format(porcentaje)
    }
    result = result.replace(",", ".")
    return result
}

fun castDate(day: Int,month: Int,year: Int): String {
    val m = month+1
    val d = if (day.toString().length == 2) day.toString() else "0$day"
    val mr = if (m.toString().length == 2) m.toString() else "0$m"
    return "$year/$mr/$d"
}

fun Fragment.hideprogress() {
    this.parentFragmentManager.fragments.takeIf { it.isNotEmpty() }?.map {
        (it as? DialogFragment)?.dismiss()
    }
}

fun Fragment.progress(mensaje: String) {
    val bundle = bundleOf(
        "mensaje" to mensaje
    )
    val dlg = DProgress()
    dlg.arguments = bundle
    dlg.isCancelable = false
    dlg.show(parentFragmentManager,"dialog")
}

fun Fragment.showDialog(titulo: String, mensaje: String, showNegativo: Boolean = false, T: () -> Unit?) {
    var positive = "Ok"
    val icon: Int = when (titulo.lowercase()) {
        "advertencia" -> R.drawable.advertencia
        "correcto" -> R.drawable.correcto
        "error" -> R.drawable.error
        else -> R.drawable.informacion
    }
    this.parentFragmentManager.fragments.takeIf { it.isNotEmpty() }?.map {
        (it as? DialogFragment)?.dismiss()
    }
    if (showNegativo) {
        positive = "De acuerdo"
    }
    MaterialDialog(this.requireContext()).show {
        icon(icon)
        title(null, titulo.uppercase())
        message(null, mensaje)
        positiveButton(null, positive) {
            dismiss()
            T()
        }
        if (showNegativo) {
            negativeButton(null, "Cancelar")
        }
    }
}

fun Activity.showDialog(titulo: String, mensaje: String, showNegativo: Boolean = false, T: () -> Unit?) {
    var positive = "Ok"
    val icon: Int = when (titulo.lowercase()) {
        "advertencia" -> R.drawable.advertencia
        "correcto" -> R.drawable.correcto
        "error" -> R.drawable.error
        else -> R.drawable.informacion
    }
    if (showNegativo) {
        positive = "De acuerdo"
    }
    MaterialDialog(this).show {
        icon(icon)
        title(null, titulo.uppercase())
        message(null, mensaje)
        cancelable(false)
        positiveButton(null, positive) {
            dismiss()
            T()
        }
        if (showNegativo) {
            negativeButton(null, "Cancelar")
        }
    }
}

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun String.checkEmail(): Boolean =
    this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.checkDocumento(tipo: String): Boolean{
    var resultado = false
    val documento = this.length
    val dni = documento == 8
    val extr = documento == 9
    val ruc = documento == 11
    when(tipo) {
        "PJ" -> if (ruc) {
            if (this.startsWith("20")){
                resultado = true
            }
        }
        "PN" -> if (dni || extr) {
            resultado = true
        }else if (ruc) {
            if (this.startsWith("10") || this.startsWith("15")){
                resultado = true
            }
        }
    }
    return resultado
}

fun String.daysBetween(today: String): String{
    val inicio = this.split(" ")[0].textToTime(6)
    val fin = today.textToTime(5)
    val diferencia = fin!!.time - inicio!!.time
    val dias = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS) + 1
    return dias.toString()
}

fun GoogleMap.settingsMap() {
    isTrafficEnabled = false
    setMaxZoomPreference(20f)
    setMinZoomPreference(10f)
    mapType = GoogleMap.MAP_TYPE_NORMAL
    uiSettings.isZoomControlsEnabled = true
    uiSettings.isZoomGesturesEnabled = true
    uiSettings.isRotateGesturesEnabled = false
    uiSettings.isZoomControlsEnabled = false
    uiSettings.isMyLocationButtonEnabled = false
}

fun GoogleMap.addingMarker(item: MarkerMap, icon: Int): Marker {
    return this.addMarker(MarkerOptions().apply {
        title(item.observacion.toString())
        snippet(item.id.toString())
        position(LatLng(item.latitud, item.longitud))
        icon(BitmapDescriptorFactory.fromResource(icon))
    })!!
}

fun GoogleMap.markerPedimap(item: Pedimap, icon: Int): Marker {
    return this.addMarker(MarkerOptions().apply {
        title("20")
        snippet(item.codigo.toString())
        position(LatLng(item.posicion.latitud, item.posicion.longitud))
        icon(BitmapDescriptorFactory.fromResource(icon))
    })!!
}

fun GoogleMap.markerBaja(item: TBajaSuper, icon: Int): Marker {
    return this.addMarker(MarkerOptions().apply {
        val titulo = "${item.clicodigo} - ${item.clinombre}"
        title(titulo)
        snippet(item.direccion)
        position(LatLng(item.latitud, item.longitud))
        icon(BitmapDescriptorFactory.fromResource(icon))
    })!!
}

fun GoogleMap.markerAlta(item: TAlta, icon: Int): Marker {
    return this.addMarker(MarkerOptions().apply {
        title("10")
        snippet(item.idaux.toString())
        position(LatLng(item.latitud, item.longitud))
        icon(BitmapDescriptorFactory.fromResource(icon))
        draggable(true)
    })!!
}

fun LatLng.toLocation(): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.latitude = this.latitude
    location.longitude = this.longitude
    return location
}

fun String.multiReplace(old: List<String>, new: String): String {
    var replaced = this
    old.forEach {
        replaced = replaced.replace(it,new)
    }
    return replaced
}

@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }

fun Date.timeToText(formato: Int, patronActual: String = ""): String {
    lateinit var result: String
    val inputPattern = if (patronActual != "") {
        patronActual
    } else {
        "EE MMM dd HH:mm:ss z yyyy"
    }
    var outputpattern = ""
    when (formato) {
        1 -> outputpattern = "dd-MM-yyyy HH:mm:ss"
        2 -> outputpattern = "dd-MM-yyyy"
        3 -> outputpattern = "HH:mm:ss"
        4 -> outputpattern = "yyyy-MM-dd HH:mm:ss"
        5 -> outputpattern = "dd/MM/yyyy"
        6 -> outputpattern = "yyyy/MM/dd"
        7 -> outputpattern = "yyyy_MM_dd_HHmmss"
        8 -> outputpattern = "yyyy-MM-dd"
        9 -> outputpattern = "yyyy-MM-dd HH:mm"
    }
    val inputFormat = SimpleDateFormat(inputPattern, Locale.ENGLISH)
    val outputFormat = SimpleDateFormat(outputpattern, Locale.ENGLISH)
    try {
        val date = inputFormat.parse(this.toString())
        result = outputFormat.format(date!!)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return result
}

fun String.textToTime(tipeformat: Int): Date? {
    var outputpattern = ""
    when (tipeformat) {
        1 -> outputpattern = "dd-MM-yyyy HH:mm:ss"
        2 -> outputpattern = "dd-MM-yyyy"
        3 -> outputpattern = "HH:mm:ss"
        4 -> outputpattern = "yyyy-MM-dd HH:mm:ss"
        5 -> outputpattern = "dd/MM/yyyy"
        6 -> outputpattern = "yyyy/MM/dd"
        7 -> outputpattern = "yyyy_MM_dd_HHmmss"
        8 -> outputpattern = "yyyy-MM-dd"
        9 -> outputpattern = "yyyy-MM-dd HH:mm"
    }
    val format = SimpleDateFormat(outputpattern, Locale.ENGLISH)
    var date: Date? = null
    try {
        date = format.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return date
}

fun View.setUI(ui: String, toggle: Boolean) {
    when (ui) {
        "v" -> visibility = if (toggle) View.VISIBLE else View.GONE
        "e" -> isEnabled = toggle
        "c" -> isClickable = toggle
        "s" -> isSelected = toggle
    }
}

fun MenuItem.setUI(ui: String, toggle: Boolean) {
    when(ui) {
        "v" -> isVisible = toggle
        "e" -> isEnabled = toggle
    }
}
