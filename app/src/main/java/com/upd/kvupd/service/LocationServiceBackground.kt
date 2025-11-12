package com.upd.kvupd.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.upd.kvupd.application.GpsNotificationHelper
import com.upd.kvupd.utils.GPSConstants.FRECUENCIA_METROS
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.GPSConstants.TRACKER_ID
import com.upd.kvupd.utils.GPSConstants.TRACKER_LAPSO_EXTENSO
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.NotificationHelper.NOTIFICATION_ID
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationServiceBackground : LifecycleService() {

    private val trackerId = TRACKER_ID
    private var modoActual = MODO_NORMAL
    private val _tag by lazy { LocationServiceBackground::class.java.simpleName }

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var gpsTracker: GpsTracker

    @Inject
    lateinit var gpsNotificationHelper: GpsNotificationHelper

    override fun onCreate() {
        super.onCreate()
        isActive = true
        Log.i(_tag, "🟢 Servicio iniciado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val nuevoModo = intent?.getStringExtra("modo") ?: MODO_NORMAL
        val modoPrevio = preferences.getString(KEY_MODO_GPS, MODO_NORMAL)

        preferences.edit().putString(KEY_MODO_GPS, nuevoModo).apply()
        modoActual = nuevoModo

        lifecycleScope.launch {
            if (modoPrevio != nuevoModo) {
                if (gpsTrackerActivo()) {
                    Log.i(_tag, "🔄 Actualizando modo de rastreo a '$nuevoModo'")
                    gpsTracker.updateTrackingConfig(trackerId, nuevoModo == MODO_EXTENSO)
                } else {
                    Log.i(_tag, "🚀 Iniciando rastreo con modo '$nuevoModo'")
                    iniciarRastreo(nuevoModo)
                }
            }

            // 🔹 Asegurar notificación visible siempre
            val notification = obtenerNotificacionPorModo(nuevoModo)
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    private fun gpsTrackerActivo(): Boolean = gpsTracker.isTracking(trackerId)

    private fun iniciarRastreo(modo: String) {
        val esExtendido = modo == MODO_EXTENSO

        gpsTracker.startTracking(
            id = trackerId,
            interval = if (esExtendido) TRACKER_LAPSO_EXTENSO else null ?: 0L,
            fastest = if (esExtendido) TRACKER_LAPSO_EXTENSO else null ?: 0L,
            minDistance = FRECUENCIA_METROS,
            onLocation = { location ->
                Log.d(_tag, "📍 Ubicación nueva: ${location.latitude}, ${location.longitude}")
            },
            onError = { error ->
                Log.e(_tag, "❌ Error en GPS: $error")
            }
        )
    }

    private fun obtenerNotificacionPorModo(modo: String): Notification =
        if (modo == MODO_EXTENSO)
            gpsNotificationHelper.mostrarModoExtendido()
        else
            gpsNotificationHelper.mostrarModoNormal()

    override fun onDestroy() {
        super.onDestroy()
        gpsTracker.stopTracking(trackerId)
        isActive = false
        Log.i(_tag, "🔴 Servicio detenido")
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    companion object {
        @Volatile private var isActive = false

        fun isRunning(): Boolean = isActive

        fun reiniciar(context: Context, modo: String = MODO_NORMAL) {
            val intent = Intent(context, LocationServiceBackground::class.java).apply {
                putExtra("modo", modo)
            }
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }
    }
}