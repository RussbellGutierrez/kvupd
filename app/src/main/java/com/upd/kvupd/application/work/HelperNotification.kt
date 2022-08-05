package com.upd.kvupd.application.work

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.upd.kvupd.R
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.utils.Constant.CONFIG_CHANNEL
import com.upd.kvupd.utils.Constant.CONFIG_NOTIF
import com.upd.kvupd.utils.Constant.DISTRITO_CHANNEL
import com.upd.kvupd.utils.Constant.DISTRITO_NOTIF
import com.upd.kvupd.utils.Constant.ENCUESTA_CHANNEL
import com.upd.kvupd.utils.Constant.ENCUESTA_NOTIF
import com.upd.kvupd.utils.Constant.MSG_CONFIG
import com.upd.kvupd.utils.Constant.MSG_DISTRITO
import com.upd.kvupd.utils.Constant.MSG_ENCUESTA
import com.upd.kvupd.utils.Constant.MSG_NEGOCIO
import com.upd.kvupd.utils.Constant.MSG_RUTA
import com.upd.kvupd.utils.Constant.MSG_USER
import com.upd.kvupd.utils.Constant.NEGOCIO_CHANNEL
import com.upd.kvupd.utils.Constant.NEGOCIO_NOTIF
import com.upd.kvupd.utils.Constant.RUTA_CHANNEL
import com.upd.kvupd.utils.Constant.RUTA_NOTIF
import com.upd.kvupd.utils.Constant.SETUP_CHANNEL
import com.upd.kvupd.utils.Constant.USER_CHANNEL
import com.upd.kvupd.utils.Constant.USER_NOTIF
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HelperNotification @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    private fun createPendingIntent(intent: Intent): PendingIntent {
        val stackBuilder = TaskStackBuilder.create(ctx)
        stackBuilder.addNextIntentWithParentStack(intent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun setupNotif(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SETUP_CHANNEL,
                "ServiceSetup",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notificacion para service setup"

            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(ctx, MainActivity::class.java)
        val pending = createPendingIntent(intent)

        val builder = NotificationCompat.Builder(ctx, SETUP_CHANNEL)
            .setSmallIcon(R.drawable.setup)
            .setContentTitle("KVentas")
            .setContentText("App running")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pending)
            .setOngoing(true)
        return builder.build()
    }

    fun configNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, CONFIG_CHANNEL)
            .setSmallIcon(R.drawable.notf_configuracion)
            .setContentTitle("KV Configuracion")
            .setContentText(MSG_CONFIG)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(CONFIG_NOTIF, notif.build())
    }

    fun userNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(USER_CHANNEL, "Usuarios", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificacion para usuarios"
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, USER_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Usuario programado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0,0,true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(USER_NOTIF, builder.build())
    }

    fun userNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, USER_CHANNEL)
            .setSmallIcon(R.drawable.notf_cliente)
            .setContentTitle("KV Usuario")
            .setContentText(MSG_USER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(USER_NOTIF, notif.build())
    }

    fun distritoNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(DISTRITO_CHANNEL, "Distritos", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificacion para distritos"
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, DISTRITO_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Distritos programados")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0,0,true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(DISTRITO_NOTIF,builder.build())
    }

    fun distritoNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, DISTRITO_CHANNEL)
            .setSmallIcon(R.drawable.notf_distrito)
            .setContentTitle("KV Distritos")
            .setContentText(MSG_DISTRITO)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(DISTRITO_NOTIF, notif.build())
    }

    fun negocioNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NEGOCIO_CHANNEL, "Negocios", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificacion para negocios"
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, NEGOCIO_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Negocios programados")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0,0,true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(NEGOCIO_NOTIF,builder.build())
    }

    fun negocioNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, NEGOCIO_CHANNEL)
            .setSmallIcon(R.drawable.notf_negocio)
            .setContentTitle("KV Negocio")
            .setContentText(MSG_NEGOCIO)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(NEGOCIO_NOTIF, notif.build())
    }

    fun rutaNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(RUTA_CHANNEL, "Rutas", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificacion para rutas"
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, RUTA_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Rutas programadas")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0,0,true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(RUTA_NOTIF,builder.build())
    }

    fun rutaNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, RUTA_CHANNEL)
            .setSmallIcon(R.drawable.notf_ruta)
            .setContentTitle("KV Ruta")
            .setContentText(MSG_RUTA)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(RUTA_NOTIF, notif.build())
    }

    fun encuestaNotifLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ENCUESTA_CHANNEL, "Encuesta", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificacion para encuesta"
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(ctx, ENCUESTA_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Encuesta programada")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0,0,true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(ctx)
        manager.notify(ENCUESTA_NOTIF,builder.build())
    }

    fun encuestaNotif() {
        val manager = NotificationManagerCompat.from(ctx)
        val notif = NotificationCompat.Builder(ctx, ENCUESTA_CHANNEL)
            .setSmallIcon(R.drawable.notf_encuesta)
            .setContentTitle("KV Encuesta")
            .setContentText(MSG_ENCUESTA)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notif.setCategory(Notification.CATEGORY_EVENT)
        manager.notify(ENCUESTA_NOTIF, notif.build())
    }
}