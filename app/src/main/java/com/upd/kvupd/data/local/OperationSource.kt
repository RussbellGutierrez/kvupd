package com.upd.kvupd.data.local

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upd.kvupd.application.receiver.GpsReceiver
import com.upd.kvupd.application.work.BootStartWorker
import com.upd.kvupd.application.work.ClientesWorker
import com.upd.kvupd.application.work.ConfiguracionWorker
import com.upd.kvupd.application.work.DistritosWorker
import com.upd.kvupd.application.work.EmpleadosWorker
import com.upd.kvupd.application.work.EncuestasWorker
import com.upd.kvupd.application.work.NegociosWorker
import com.upd.kvupd.application.work.RutasWorker
import com.upd.kvupd.service.LocationServiceBackground
import com.upd.kvupd.ui.sealed.TipoUsuario
import com.upd.kvupd.utils.AlarmConstants.ALARMA_FIN
import com.upd.kvupd.utils.AlarmConstants.ALARMA_INICIO
import com.upd.kvupd.utils.GPSConstants.MODO_EXTENSO
import com.upd.kvupd.utils.GPSConstants.MODO_NORMAL
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_FIN
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_INICIO
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_MODO_GPS
import com.upd.kvupd.utils.toLocalTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID
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

    fun lanzarWorkersRestantes(usuarioTipo: String): List<UUID> {
        val tipo = TipoUsuario.inicialTipo(usuarioTipo)

        val lista: List<OneTimeWorkRequest> = when (tipo) {
            TipoUsuario.Vendedor -> listOf(
                workerClientes(),
                workerDistritos(),
                workerNegocios(),
                workerRutas()
            )

            TipoUsuario.Supervisor -> listOf(
                workerEmpleados(),
                workerDistritos(),
                workerNegocios(),
                workerRutas()
            )
        }

        val encuestas = workerEncuestas()

        // 🔹 Encola la cadena: (clientes/empleados + distritos + negocios + rutas) → encuestas
        workManager
            .beginWith(lista)
            .then(encuestas)
            .enqueue()

        // 🔹 Devuelve todos los IDs de los workers recién encolados
        return lista.map { it.id } + encuestas.id
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

    @SuppressLint("NewApi")
    fun sincronizarModoYAlarmas() {
        val ahora = LocalTime.now()
        val horaInicio = preferences.getString(KEY_HORA_INICIO, "07:00:00")!!.toLocalTime()
        val horaFin = preferences.getString(KEY_HORA_FIN, "22:00:00")!!.toLocalTime()

        val dentroHorario = ahora.isAfter(horaInicio) && ahora.isBefore(horaFin)
        val modoNuevo = if (dentroHorario) MODO_NORMAL else MODO_EXTENSO
        val modoActual = preferences.getString(KEY_MODO_GPS, MODO_NORMAL)

        val serviceActivo = LocationServiceBackground.isRunning()
        if (!serviceActivo || modoNuevo != modoActual) {
            LocationServiceBackground.reiniciar(context, modoNuevo)
        }

        if (!dentroHorario) {
            reprogramarAlarmas(horaInicio, horaFin)
        }
    }

    private fun reprogramarAlarmas(horaInicio: LocalTime, horaFin: LocalTime) {

        // 🔹 Cancelar alarmas previas
        cancelarAlarma(ALARMA_INICIO)
        cancelarAlarma(ALARMA_FIN)

        // 🔹 Crear Intent para activar modo normal
        val intentInicio = Intent(context, GpsReceiver::class.java).apply {
            action = "com.upd.kvupd.CHANGE_MODE"
            putExtra("modo", MODO_NORMAL)
        }
        programarAlarma(intentInicio, horaInicio, requestCode = 1001)

        // 🔹 Crear Intent para activar modo extendido
        val intentFin = Intent(context, GpsReceiver::class.java).apply {
            action = "com.upd.kvupd.CHANGE_MODE"
            putExtra("modo", MODO_EXTENSO)
        }
        programarAlarma(intentFin, horaFin, requestCode = 1002)
    }

    @SuppressLint("MissingPermission", "NewApi")
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

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.i(
            "AlarmasGPS",
            "⏰ Alarma programada: ${intent.getStringExtra("modo")} -> ${calendar.time}"
        )
    }

    private fun cancelarAlarma(tipo: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = if (tipo == ALARMA_INICIO) 1001 else 1002

        val intent = Intent(context, GpsReceiver::class.java).apply {
            action = "com.upd.kvupd.CHANGE_MODE"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.i("AlarmasGPS", "❌ Alarma cancelada ($tipo)")
        }
    }
}