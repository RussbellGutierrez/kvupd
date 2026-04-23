package com.upd.kvupd.data.local

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.upd.kvupd.application.receiver.GpsReceiver
import com.upd.kvupd.application.work.BootStartWorker
import com.upd.kvupd.application.work.CleanupWorker
import com.upd.kvupd.application.work.ClientesWorker
import com.upd.kvupd.application.work.ConfiguracionWorker
import com.upd.kvupd.application.work.CoreCsvWorker
import com.upd.kvupd.application.work.DistritosWorker
import com.upd.kvupd.application.work.EmpleadosWorker
import com.upd.kvupd.application.work.EncuestasWorker
import com.upd.kvupd.application.work.NegociosWorker
import com.upd.kvupd.application.work.RutasWorker
import com.upd.kvupd.application.work.ServidorWorker
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.service.LocationServiceBackground
import com.upd.kvupd.utils.AlarmConstants.REQUEST_CODE_ALARMA_FIN
import com.upd.kvupd.utils.AlarmConstants.REQUEST_CODE_ALARMA_INICIO
import com.upd.kvupd.utils.AlarmConstants.WINDOW_ALARMA_GPS
import com.upd.kvupd.utils.ConstantsExtras.GPS_FLOW
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants.INTENT_EXTRA_GPS
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.NotificationHelper.ACTION_CHANGE_MODE
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_FIN
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_INICIO
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import com.upd.kvupd.utils.toLocalTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OperationSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val alarmManager: AlarmManager,
    private val preferences: SharedPreferences
) {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun lanzarWorkerReinicio() {
        workManager.enqueue(bootWorker())
    }

    fun lanzarWorkerInicial(): UUID {
        val configuracion = workerConfiguracion()
        workManager.enqueue(configuracion)
        return configuracion.id
    }

    fun lanzarServidorWorker() {
        workManager.enqueueUniquePeriodicWork(
            "servidor_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workerServidor()
        )
    }

    fun lanzarCleanupWorker() {
        workManager.enqueueUniquePeriodicWork(
            "cleanup_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workerCleanupPeriodic()
        )
    }

    fun lanzarCleanupNow() {
        workManager.enqueueUniqueWork(
            "cleanup_now",
            ExistingWorkPolicy.REPLACE,
            workerCleanupNow()
        )
    }

    fun lanzarCoreCsvWorker() {
        workManager.enqueueUniqueWork(
            "core_csv_worker",
            ExistingWorkPolicy.KEEP,
            workerCoreCsv()
        )
    }

    fun lanzarWorkersRestantes(usuarioTipo: TipoUsuario): List<UUID> {
        val comunes = listOf(
            workerDistritos(),
            workerNegocios(),
            workerRutas()
        )

        val especificos = when (usuarioTipo) {
            TipoUsuario.VENDEDOR -> listOf(workerClientes())
            TipoUsuario.SUPERVISOR -> listOf(workerEmpleados())
            TipoUsuario.JEFE_VENTAS -> emptyList()
        }

        val lista = especificos + comunes
        val encuestas = workerEncuestas()

        return if (lista.isEmpty()) {
            workManager.enqueue(encuestas)
            listOf(encuestas.id)
        } else {
            workManager
                .beginWith(lista)
                .then(encuestas)
                .enqueue()

            lista.map { it.id } + encuestas.id
        }
    }

    private fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<ConfiguracionWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerClientes() =
        OneTimeWorkRequestBuilder<ClientesWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerEmpleados() =
        OneTimeWorkRequestBuilder<EmpleadosWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerRutas() =
        OneTimeWorkRequestBuilder<RutasWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerEncuestas() =
        OneTimeWorkRequestBuilder<EncuestasWorker>()
            .setConstraints(constraints)
            .build()

    private fun bootWorker() =
        OneTimeWorkRequestBuilder<BootStartWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerServidor() =
        PeriodicWorkRequestBuilder<ServidorWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

    private fun workerCleanupPeriodic() =
        PeriodicWorkRequestBuilder<CleanupWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

    private fun workerCleanupNow() =
        OneTimeWorkRequestBuilder<CleanupWorker>()
            .setConstraints(constraints)
            .build()

    private fun workerCoreCsv() =
        OneTimeWorkRequestBuilder<CoreCsvWorker>()
            .setConstraints(constraints)
            .build()

    fun syncInicial(config: TableConfiguracion) {
        Log.e(GPS_FLOW, "[SYNC] syncInicial ejecutado")

        val ahora = LocalTime.now()
        val horaInicio = preferences.getString(KEY_HORA_INICIO, "07:00:00")!!.toLocalTime()
        val horaFin = preferences.getString(KEY_HORA_FIN, "22:00:00")!!.toLocalTime()

        val dentroHorario = if (horaInicio.isBefore(horaFin)) {
            !ahora.isBefore(horaInicio) && ahora.isBefore(horaFin)
        } else {
            !ahora.isBefore(horaInicio) || ahora.isBefore(horaFin)
        }

        val esHoy = FechaHoraUtil.esHoy(config.fecha)
        var modoNuevo = if (dentroHorario) MODO_NORMAL else MODO_EXTENSO

        if (!esHoy) {
            modoNuevo = MODO_EXTENSO
        }

        // 🔹 Persistes modo SIEMPRE
        preferences.edit()
            .putString(KEY_MODO_GPS, modoNuevo)
            .apply()

        Log.e(GPS_FLOW, "[SYNC] asegurando service → modo=$modoNuevo")

        // 🔥 SIEMPRE lanzas el service
        LocationServiceBackground.reiniciar(context, modoNuevo)
    }

    fun reprogramarPorNuevaConfig() {
        Log.e(GPS_FLOW, "[SYNC] reprogramarPorNuevaConfig ejecutado")

        val horaInicio = preferences.getString(KEY_HORA_INICIO, "07:00:00")!!.toLocalTime()
        val horaFin = preferences.getString(KEY_HORA_FIN, "22:00:00")!!.toLocalTime()

        reprogramarAlarmas(horaInicio, horaFin)
    }

    private fun reprogramarAlarmas(horaInicio: LocalTime, horaFin: LocalTime) {

        // 🔹 Cancelar alarmas previas
        cancelarAlarmaPorModo(MODO_NORMAL)
        cancelarAlarmaPorModo(MODO_EXTENSO)

        // 🔹 Crear Intent para activar modo normal
        val intentInicio = buildIntent(MODO_NORMAL)
        programarAlarma(intentInicio, horaInicio, REQUEST_CODE_ALARMA_INICIO)

        // 🔹 Crear Intent para activar modo extendido
        val intentFin = buildIntent(MODO_EXTENSO)
        programarAlarma(intentFin, horaFin, REQUEST_CODE_ALARMA_FIN)
    }

    @SuppressLint("MissingPermission")
    private fun programarAlarma(
        intent: Intent,
        hora: LocalTime,
        requestCode: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora.hour)
            set(Calendar.MINUTE, hora.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(
                Calendar.DATE,
                1
            ) // programar para mañana si ya pasó
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            WINDOW_ALARMA_GPS,
            pendingIntent
        )

        Log.e(
            GPS_FLOW,
            "[AlarmasGPS] ⏰ Alarma programada: ${intent.getStringExtra(INTENT_EXTRA_GPS)} -> ${calendar.time}"
        )
    }

    private fun cancelarAlarmaPorModo(modo: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val requestCode = if (modo == MODO_NORMAL)
            REQUEST_CODE_ALARMA_INICIO
        else
            REQUEST_CODE_ALARMA_FIN

        val intent = buildIntent(modo)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.e(GPS_FLOW, "[AlarmasGPS] ❌ Alarma cancelada (modo=$modo)")
        }
    }

    fun sesionActual(config: TableConfiguracion): Boolean {
        val fecha = config.fecha
        return FechaHoraUtil.esHoy(fecha)
    }

    fun validarYRecrearAlarmasSiFaltan() {
        val existeInicio = existeAlarma(MODO_NORMAL)
        val existeFin = existeAlarma(MODO_EXTENSO)

        if (!existeInicio || !existeFin) {
            val horaInicio = preferences.getString(KEY_HORA_INICIO, "07:00:00")!!.toLocalTime()
            val horaFin = preferences.getString(KEY_HORA_FIN, "22:00:00")!!.toLocalTime()

            reprogramarAlarmas(horaInicio, horaFin)
        }
    }

    private fun existeAlarma(modo: String): Boolean {
        val requestCode = if (modo == MODO_NORMAL)
            REQUEST_CODE_ALARMA_INICIO
        else
            REQUEST_CODE_ALARMA_FIN

        val intent = buildIntent(modo)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent != null
    }

    fun programarSiguienteAlarma(modo: String) {
        val horaInicio = preferences.getString(KEY_HORA_INICIO, "07:00:00")!!.toLocalTime()
        val horaFin = preferences.getString(KEY_HORA_FIN, "22:00:00")!!.toLocalTime()

        if (modo == MODO_NORMAL) {
            programarFin(horaFin) // Se ejecutó INICIO → toca FIN
        } else {
            programarInicio(horaInicio) // Se ejecutó FIN → toca INICIO
        }
    }

    private fun programarInicio(horaInicio: LocalTime) {
        val intent = buildIntent(MODO_NORMAL)
        programarAlarma(intent, horaInicio, REQUEST_CODE_ALARMA_INICIO)
    }

    private fun programarFin(horaFin: LocalTime) {
        val intent = buildIntent(MODO_EXTENSO)
        programarAlarma(intent, horaFin, REQUEST_CODE_ALARMA_FIN)
    }

    private fun buildIntent(modo: String) =
        Intent(context, GpsReceiver::class.java).apply {
            action = ACTION_CHANGE_MODE
            putExtra(INTENT_EXTRA_GPS, modo)
        }
}