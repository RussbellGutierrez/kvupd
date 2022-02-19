package com.upd.kv.domain

import android.graphics.Bitmap
import androidx.work.OneTimeWorkRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kv.data.model.MarkerMap

interface Functions {
    fun generateQR(value: String): Bitmap
    fun saveQR(bm: Bitmap)
    fun existQR(): Boolean
    fun parseQRtoIMEI(add: Boolean = false): String
    fun getQR(): Bitmap
    fun dateToday(formato: Int): String
    fun appSO(): String
    fun setupMarkers(map: GoogleMap,list: List<MarkerMap>): List<Marker>
    fun executeService(service: String, foreground: Boolean)
    fun launchWorkers()
    fun workerSetup(long: Long)
    fun workerConfiguracion(): OneTimeWorkRequest
    fun workerUser(): OneTimeWorkRequest
    fun workerDistritos(): OneTimeWorkRequest
    fun workerNegocios(): OneTimeWorkRequest
}