package com.upd.kvupd.domain

import android.graphics.Bitmap
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.MarkerMap
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.data.model.TIncidencia

interface OldFunctions {
    fun generateQR(value: String): Bitmap
    fun saveQR(bm: Bitmap)
    fun existQR(): Boolean
    fun addIPtoQRIMEI()
    fun parseQRtoIP(): String
    fun parseQRtoIMEI(add: Boolean = false): String
    fun getQR(): Bitmap?
    fun appSO(): String
    fun isConnected(): Boolean
    fun deleteFotos()
    fun isSunday(): Boolean
    fun filterListCliente(list: List<DataCliente>): List<DataCliente>
    fun mobileInternetState()
    fun enableBroadcastGPS()
    fun checkGPSEnabled()
    fun enableBatteryChange()
    fun saveSystemActions(tipo: String, msg: String?): TIncidencia?

    fun setupMarkers(map: GoogleMap, list: List<MarkerMap>): List<Marker>
    fun pedimapMarkers(map: GoogleMap, list: List<Pedimap>): List<Marker>
    fun altaMarkers(map: GoogleMap, list: List<TAlta>): List<Marker>
    fun bajaMarker(map: GoogleMap, baja: TBajaSuper): List<Marker>
    fun consultaMarker(map: GoogleMap, list: List<TConsulta>): List<Marker>

    fun executeService(service: String, foreground: Boolean)
    fun constrainsWork(): Constraints
    fun launchWorkers()

    fun closePeriodicWorker()
    fun alarmSetup(long: Long)
    fun alarmFinish(long: Long)

    fun workerConfiguracion(): OneTimeWorkRequest
    fun workerUser(): OneTimeWorkRequest
    fun workerDistritos(): OneTimeWorkRequest
    fun workerNegocios(): OneTimeWorkRequest
    fun workerRutas(): OneTimeWorkRequest
    fun workerEncuestas(): OneTimeWorkRequest

    fun workerperVisita()
    fun workerperAlta()
    fun workerperAltaEstado()
    fun workerperBaja()
    fun workerperBajaEstado()
    fun workerperRespuesta()
    fun workerperFoto()
    fun workerperAltaFoto()

    fun closeAllNotifications()
}