package com.upd.kvupd.application

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.upd.kvupd.R
import com.upd.kvupd.application.receiver.GpsReceiver
import com.upd.kvupd.utils.ConstantsExtras.GPS_FLOW
import com.upd.kvupd.utils.GPSConstants.GPS_CHANNEL
import com.upd.kvupd.utils.GPSConstants.GPS_NOTIF_ID
import com.upd.kvupd.utils.NotificationHelper.ACTION_OPEN_APP
import com.upd.kvupd.utils.NotificationHelper.ACTION_RECREATE_NOTIFICATION
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsNotificationHelper @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val notificationManager: NotificationManager
) {
    private val channelId = GPS_CHANNEL
    private val notificationId = GPS_NOTIF_ID

    init {
        crearCanal()
    }

    private fun crearCanal() {
        val channel = NotificationChannel(
            channelId,
            "Servicio GPS",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Ubicacion en segundo plano"
            enableLights(true)
            enableVibration(true)
            lightColor = Color.BLUE
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun pendingIntentAbrirApp(): PendingIntent {
        val intent = Intent(ctx, GpsReceiver::class.java).apply {
            action = ACTION_OPEN_APP
        }
        return PendingIntent.getBroadcast(
            ctx,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun pendingIntentReactivar(): PendingIntent {
        val intent = Intent(ctx, GpsReceiver::class.java).apply {
            action = ACTION_RECREATE_NOTIFICATION
        }
        return PendingIntent.getBroadcast(
            ctx,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun mostrarModoNormal(): Notification {
        Log.e(GPS_FLOW, "[GPS_NOTIF] 🔔 creando notificación MODO_NORMAL")
        return buildNotification(
            texto = "Aplicacion activa",
            icono = R.drawable.emitir
        )
    }

    fun mostrarModoExtendido(): Notification {
        Log.e(GPS_FLOW, "[GPS_NOTIF] 🔔 creando notificación MODO_EXTENSO")
        return buildNotification(
            texto = "Fuera de hora laboral",
            icono = R.drawable.dormir
        )
    }

    private fun buildNotification(texto: String, icono: Int): Notification {
        return NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(icono)
            .setContentTitle("KVentas")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setColor(Color.RED)
            .setContentIntent(pendingIntentAbrirApp())
            .setDeleteIntent(pendingIntentReactivar())
            .build()
    }

    @SuppressLint("MissingPermission")
    fun actualizarNotificacion(notification: Notification) {
        Log.e(GPS_FLOW, "[GPS_NOTIF] ♻️ notificación actualizada")
        notificationManager.notify(notificationId, notification)
    }
}