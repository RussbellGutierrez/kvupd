package com.upd.kvupd.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.upd.kvupd.application.GpsNotificationHelper
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.utils.ConstantsExtras.GPS_FLOW
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants.DISTANCIA_NORMAL
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.GPSConstants.TRACKER_GPS
import com.upd.kvupd.utils.GPSConstants.GPT_LAPSO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.INTENT_EXTRA_GPS
import com.upd.kvupd.utils.GPSConstants.DISTANCIA_EXTENSO
import com.upd.kvupd.utils.GPSConstants.GPT_INTERVALO_NORMAL
import com.upd.kvupd.utils.GPSConstants.GPT_INTERVALO_RAPIDO
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.NotificationHelper.NOTIFICATION_ID
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import com.upd.kvupd.utils.to2Decimals
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LocationServiceBackground : LifecycleService() {

    @Volatile //Controla la visibilidad entre hilos
    private var modoActual = MODO_NORMAL

    private var codigoUsuario: String? = null
    private val _tag by lazy { LocationServiceBackground::class.java.simpleName }

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var gpsTracker: GpsTracker

    @Inject
    lateinit var gpsNotificationHelper: GpsNotificationHelper

    @Inject
    lateinit var roomFunction: RoomFunctions

    override fun onCreate() {
        super.onCreate()
        Log.i(_tag, "🟢 Servicio iniciado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val nuevoModo = intent?.getStringExtra(INTENT_EXTRA_GPS) ?: MODO_NORMAL
        val modoPrevio = preferences.getString(KEY_MODO_GPS, MODO_NORMAL)

        // Persistir modo (Main)
        preferences.edit().putString(KEY_MODO_GPS, nuevoModo).apply()
        modoActual = nuevoModo

        val notification = obtenerNotificacionPorModo(nuevoModo)

        // ⚠️ Foreground inmediato
        startForeground(NOTIFICATION_ID, notification)

        lifecycleScope.launch {

            // 🔹 Room en IO
            if (codigoUsuario == null) {
                codigoUsuario = withContext(Dispatchers.IO) {
                    roomFunction.queryConfiguracion()?.codigo
                }
            }

            val trackingActivo = gpsTracker.isTracking(TRACKER_GPS)

            Log.i(
                _tag,
                "Modo GPS: $modoPrevio -> $nuevoModo | tracking=${trackingActivo}"
            )

            // 🔹 Early-exit solo de la coroutine
            if (modoPrevio == nuevoModo && trackingActivo) {
                Log.i(_tag, "Sin cambios de modo y tracking activo")
                return@launch
            }

            if (trackingActivo) {
                gpsTracker.updateTrackingConfig(
                    TRACKER_GPS,
                    nuevoModo == MODO_EXTENSO
                )
                gpsNotificationHelper.actualizarNotificacion(notification)
            } else {
                iniciarRastreo(nuevoModo)
            }
        }

        return START_STICKY
    }

    private fun iniciarRastreo(modo: String) {
        val modoExtenso = modo == MODO_EXTENSO

        gpsTracker.startTracking(
            id = TRACKER_GPS,
            interval = if (modoExtenso) GPT_LAPSO_EXTENSO else GPT_INTERVALO_NORMAL,
            fastest = if (modoExtenso) GPT_LAPSO_EXTENSO else GPT_INTERVALO_RAPIDO,
            minDistance = if (modoExtenso) DISTANCIA_EXTENSO else DISTANCIA_NORMAL,
            onLocation = { location ->
                Log.d(_tag, "📍 ${location.latitude}, ${location.longitude}")

                if (modoActual == MODO_NORMAL && codigoUsuario != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val bateria = obtenerNivelBateria()

                            val item = TableSeguimiento(
                                fecha = FechaHoraUtil.ahora(),
                                usuario = codigoUsuario!!,
                                longitud = location.longitude,
                                latitud = location.latitude,
                                precision = location.accuracy.toDouble().to2Decimals(),
                                bateria = bateria
                            )
                            roomFunction.saveSeguimiento(item)
                        } catch (e: Exception) {
                            Log.e(_tag, "❌ Error guardando seguimiento", e)
                        }
                    }
                } else {
                    Log.d(
                        _tag,
                        "⏭ Ubicación ignorada (modo=$modoActual, usuario=$codigoUsuario)"
                    )
                }
            },
            onError = { error ->
                Log.e(_tag, "❌ Error GPS: $error")
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        gpsTracker.stopTracking(TRACKER_GPS)
        Log.i(_tag, "🔴 Servicio detenido")
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun obtenerNotificacionPorModo(modo: String): Notification =
        if (modo == MODO_EXTENSO)
            gpsNotificationHelper.mostrarModoExtendido()
        else
            gpsNotificationHelper.mostrarModoNormal()

    private fun obtenerNivelBateria(): Int {
        val batteryManager =
            getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val nivel = batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )

        return if (nivel >= 0) nivel else 0
    }

    companion object {
        fun reiniciar(context: Context, modo: String = MODO_NORMAL) {
            Log.e(GPS_FLOW, "[SERVICE] reiniciar() llamado → modo=$modo")

            val intent = Intent(context, LocationServiceBackground::class.java).apply {
                putExtra(INTENT_EXTRA_GPS, modo)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}