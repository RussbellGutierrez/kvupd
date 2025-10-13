package com.upd.kvupd.application.work

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.upd.kvupd.R
import com.upd.kvupd.application.OldReceiver
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.utils.OldConstant.ACTION_NOTIFICATION_DISMISSED
import com.upd.kvupd.utils.OldConstant.ACTION_NOTIFICATION_SLEEPED
import com.upd.kvupd.utils.OldConstant.CONFIG_CHANNEL
import com.upd.kvupd.utils.OldConstant.CONFIG_NOTIF
import com.upd.kvupd.utils.OldConstant.DISMISS_ID
import com.upd.kvupd.utils.OldConstant.DISMISS_NAME
import com.upd.kvupd.utils.OldConstant.DISTRITO_CHANNEL
import com.upd.kvupd.utils.OldConstant.DISTRITO_NOTIF
import com.upd.kvupd.utils.OldConstant.ENCUESTA_CHANNEL
import com.upd.kvupd.utils.OldConstant.ENCUESTA_NOTIF
import com.upd.kvupd.utils.OldConstant.MSG_CONFIG
import com.upd.kvupd.utils.OldConstant.MSG_DISTRITO
import com.upd.kvupd.utils.OldConstant.MSG_ENCUESTA
import com.upd.kvupd.utils.OldConstant.MSG_NEGOCIO
import com.upd.kvupd.utils.OldConstant.MSG_RUTA
import com.upd.kvupd.utils.OldConstant.MSG_USER
import com.upd.kvupd.utils.OldConstant.NEGOCIO_CHANNEL
import com.upd.kvupd.utils.OldConstant.NEGOCIO_NOTIF
import com.upd.kvupd.utils.OldConstant.RUTA_CHANNEL
import com.upd.kvupd.utils.OldConstant.RUTA_NOTIF
import com.upd.kvupd.utils.OldConstant.SETUP_CHANNEL
import com.upd.kvupd.utils.OldConstant.SLEEP_ID
import com.upd.kvupd.utils.OldConstant.SLEEP_NAME
import com.upd.kvupd.utils.OldConstant.USER_CHANNEL
import com.upd.kvupd.utils.OldConstant.USER_NOTIF
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OldHelperNotification @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val notificationManager: NotificationManager
) {

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(ctx, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(ctx)
        stackBuilder.addNextIntentWithParentStack(intent)
        return stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDeletePending(): PendingIntent {
        val intent = Intent(ctx, OldReceiver::class.java)
        intent.action = ACTION_NOTIFICATION_DISMISSED
        intent.putExtra(DISMISS_NAME, DISMISS_ID)
        return PendingIntent.getBroadcast(
            ctx,
            DISMISS_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createSleepPending(): PendingIntent {
        val intent = Intent(ctx, OldReceiver::class.java)
        intent.action = ACTION_NOTIFICATION_SLEEPED
        intent.putExtra(SLEEP_NAME, SLEEP_ID)
        return PendingIntent.getBroadcast(
            ctx,
            SLEEP_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setupNotif(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SETUP_CHANNEL,
                "ServiceSetup",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para service setup"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, SETUP_CHANNEL)
            .setSmallIcon(R.drawable.setup)
            .setLargeIcon(AppCompatResources.getDrawable(ctx, R.drawable.setup)?.toBitmap())
            .setContentTitle("KVentas")
            .setContentText("App running")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(createPendingIntent())
            .setDeleteIntent(createDeletePending())
            .setOngoing(true)
        return builder.build()
    }

    fun sleepNotif(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SETUP_CHANNEL,
                "ServiceSetup",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para service setup"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, SETUP_CHANNEL)
            .setSmallIcon(R.drawable.dormir)
            .setContentTitle("KVentas")
            .setContentText("Sleep service")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(createPendingIntent())
            .setDeleteIntent(createSleepPending())
            .setOngoing(true)
        return builder.build()
    }

    @SuppressLint("MissingPermission")
    fun configNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CONFIG_CHANNEL,
                "Configuracion",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para configuracion"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, CONFIG_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Configuracion para el equipo")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(CONFIG_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun configNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, CONFIG_CHANNEL)
            .setSmallIcon(R.drawable.notf_configuracion)
            .setContentTitle("KV Configuracion")
            .setContentText(MSG_CONFIG)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_CONFIG)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(CONFIG_NOTIF, notif.build())

        if (MSG_CONFIG.contains("*")) {
            Handler(Looper.getMainLooper()).postDelayed({
                manager.cancel(CONFIG_NOTIF)
            }, 5000)
        }
    }

    @SuppressLint("MissingPermission")
    fun userNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                USER_CHANNEL,
                "Usuarios",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para usuarios"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, USER_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Usuario programado")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(USER_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun userNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, USER_CHANNEL)
            .setSmallIcon(R.drawable.notf_cliente)
            .setContentTitle("KV Usuario")
            .setContentText(MSG_USER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_USER)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(USER_NOTIF, notif.build())

        if (MSG_USER.contains("*")) {
            Handler(Looper.getMainLooper()).postDelayed({
                manager.cancel(USER_NOTIF)
            }, 5000)
        }
    }

    @SuppressLint("MissingPermission")
    fun distritoNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DISTRITO_CHANNEL,
                "Distritos",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para distritos"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, DISTRITO_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Distritos programados")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(DISTRITO_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun distritoNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, DISTRITO_CHANNEL)
            .setSmallIcon(R.drawable.notf_distrito)
            .setContentTitle("KV Distritos")
            .setContentText(MSG_DISTRITO)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_DISTRITO)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(DISTRITO_NOTIF, notif.build())

        if (MSG_DISTRITO.contains("*")) {
            Handler(Looper.getMainLooper()).postDelayed({
                manager.cancel(DISTRITO_NOTIF)
            }, 5000)
        }
    }

    @SuppressLint("MissingPermission")
    fun negocioNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NEGOCIO_CHANNEL,
                "Negocios",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para negocios"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, NEGOCIO_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Negocios programados")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(NEGOCIO_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun negocioNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, NEGOCIO_CHANNEL)
            .setSmallIcon(R.drawable.notf_negocio)
            .setContentTitle("KV Negocio")
            .setContentText(MSG_NEGOCIO)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_NEGOCIO)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(NEGOCIO_NOTIF, notif.build())

        if (MSG_NEGOCIO.contains("*")) {
            Handler(Looper.getMainLooper()).postDelayed({
                manager.cancel(NEGOCIO_NOTIF)
            }, 5000)
        }
    }

    @SuppressLint("MissingPermission")
    fun rutaNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                RUTA_CHANNEL,
                "Rutas",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para rutas"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, RUTA_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Rutas programadas")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(RUTA_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun rutaNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, RUTA_CHANNEL)
            .setSmallIcon(R.drawable.notf_ruta)
            .setContentTitle("KV Ruta")
            .setContentText(MSG_RUTA)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_RUTA)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(RUTA_NOTIF, notif.build())

        if (MSG_RUTA.contains("*")) {
            Handler(Looper.getMainLooper()).postDelayed({
                manager.cancel(RUTA_NOTIF)
            }, 5000)
        }
    }

    @SuppressLint("MissingPermission")
    fun encuestaNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ENCUESTA_CHANNEL,
                "Encuesta",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificacion para encuesta"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, ENCUESTA_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Encuesta programada")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(ENCUESTA_NOTIF, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun encuestaNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, ENCUESTA_CHANNEL)
            .setSmallIcon(R.drawable.notf_encuesta)
            .setContentTitle("KV Encuesta")
            .setContentText(MSG_ENCUESTA)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(MSG_ENCUESTA)
            )
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_SERVICE)
        manager.notify(ENCUESTA_NOTIF, notif.build())

        Handler(Looper.getMainLooper()).postDelayed({
            manager.cancel(ENCUESTA_NOTIF)
        }, 5000)
    }
}