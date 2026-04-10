package com.upd.kvupd.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat
import com.upd.kvupd.application.GpsNotificationHelper
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.service.LocationServiceBackground
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.utils.ConstantsExtras.GPS_FLOW
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants.INTENT_EXTRA_GPS
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.NotificationHelper.ACTION_CHANGE_MODE
import com.upd.kvupd.utils.NotificationHelper.ACTION_OPEN_APP
import com.upd.kvupd.utils.NotificationHelper.ACTION_RECREATE_NOTIFICATION
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GpsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var operationsFunctions: OperationsFunctions

    @Inject
    lateinit var notifHelper: GpsNotificationHelper

    @Inject
    lateinit var preference: SharedPreferences

    @Inject
    lateinit var roomFunctions: RoomFunctions

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                handleIntent(context, intent)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleIntent(context: Context, intent: Intent) {

        Log.e(
            GPS_FLOW,
            "[GPS_RECEIVER] 📡 onReceive | action=${intent.action} | hora=${System.currentTimeMillis()}"
        )

        when (intent.action) {

            ACTION_OPEN_APP -> {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(launchIntent)
            }

            ACTION_RECREATE_NOTIFICATION -> {
                val modo = preference.getString(KEY_MODO_GPS, MODO_NORMAL) ?: MODO_NORMAL

                val notif = if (modo == MODO_EXTENSO)
                    notifHelper.mostrarModoExtendido()
                else
                    notifHelper.mostrarModoNormal()

                notifHelper.actualizarNotificacion(notif)

                val restartIntent = Intent(context, LocationServiceBackground::class.java).apply {
                    putExtra(INTENT_EXTRA_GPS, modo)
                }

                ContextCompat.startForegroundService(context, restartIntent)
            }

            ACTION_CHANGE_MODE -> {

                // 🔹 modo que viene de la alarma
                var modo = intent.getStringExtra(INTENT_EXTRA_GPS)
                    ?: preference.getString(KEY_MODO_GPS, MODO_NORMAL)
                    ?: MODO_NORMAL

                Log.e(GPS_FLOW, "[GPS_RECEIVER] ⏰ ALARM EJECUTADO → modo=$modo")

                // 🔥 validar si la config es de hoy
                val config = roomFunctions.queryConfiguracion()
                val esHoy = config != null && FechaHoraUtil.esHoy(config.fecha)

                if (!esHoy) {
                    modo = MODO_EXTENSO
                }

                // 🔹 guardar modo final
                preference.edit()
                    .putString(KEY_MODO_GPS, modo)
                    .apply()

                // 🔹 reiniciar servicio con modo correcto
                LocationServiceBackground.reiniciar(context, modo)

                // 🔹 programar siguiente alarma
                operationsFunctions.programNextAlarm(modo)

                // 🔹 actualizar notificación
                val notif = if (modo == MODO_EXTENSO)
                    notifHelper.mostrarModoExtendido()
                else
                    notifHelper.mostrarModoNormal()

                notifHelper.actualizarNotificacion(notif)
            }
        }
    }
}