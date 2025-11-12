package com.upd.kvupd.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.upd.kvupd.application.GpsNotificationHelper
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.service.LocationServiceBackground
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.NotificationHelper.ACTION_CHANGE_MODE
import com.upd.kvupd.utils.NotificationHelper.ACTION_OPEN_APP
import com.upd.kvupd.utils.NotificationHelper.ACTION_RECREATE_NOTIFICATION
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GpsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notifHelper: GpsNotificationHelper

    @Inject
    lateinit var preference: SharedPreferences

    @Inject
    lateinit var operationsFunctions: OperationsFunctions

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            ACTION_OPEN_APP -> {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(launchIntent)
            }

            ACTION_RECREATE_NOTIFICATION -> {
                val modo = preference.getString(KEY_MODO_GPS, MODO_NORMAL) ?: MODO_NORMAL

                val notif = when (modo) {
                    MODO_EXTENSO -> notifHelper.mostrarModoExtendido()
                    else -> notifHelper.mostrarModoNormal()
                }

                notifHelper.actualizarNotificacion(notif)

                val restartIntent = Intent(context, LocationServiceBackground::class.java).apply {
                    putExtra("modo", modo)
                }
                ContextCompat.startForegroundService(context, restartIntent)
            }

            ACTION_CHANGE_MODE -> {
                operationsFunctions.syncModeAlarms()

                val modo = preference.getString(KEY_MODO_GPS, MODO_NORMAL) ?: MODO_NORMAL

                val notif = if (modo == MODO_EXTENSO)
                    notifHelper.mostrarModoExtendido()
                else
                    notifHelper.mostrarModoNormal()

                notifHelper.actualizarNotificacion(notif)
            }
        }
    }
}