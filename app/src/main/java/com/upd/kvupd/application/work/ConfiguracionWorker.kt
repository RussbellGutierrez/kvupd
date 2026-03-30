package com.upd.kvupd.application.work

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_FIN
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_HORA_INICIO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ConfiguracionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val identityFunctions: IdentityFunctions,
    private val roomFunctions: RoomFunctions,
    private val serverFunctions: ServerFunctions,
    private val jsobFunctions: JsObFunctions,
    private val preferences: SharedPreferences
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val uuid = identityFunctions.obtenerIdentificador()
                ?: return Result.failure(workDataOf("error" to "No se encontro UUID valido"))

            val json = jsobFunctions.jsonObjectConfiguracion(uuid)
            serverFunctions.apiDownloadConfiguracion(json).collect { resultado ->
                when (resultado) {
                    is ResultadoApi.Loading -> {
                        setProgressAsync(workDataOf("estado" to "Descargando configuracion..."))
                        kotlinx.coroutines.delay(300) // 👈 deja respirar el LiveData
                    }

                    is ResultadoApi.Exito -> {
                        val jobl = resultado.data?.jobl

                        if (jobl.isNullOrEmpty()) {
                            setProgressAsync(workDataOf("estado" to "Configuracion retorno vacia"))
                            return@collect
                        }

                        kotlinx.coroutines.delay(300)

                        roomFunctions.replaceConfiguracion(jobl)

                        // 🔹 Guardar horarios en SharedPreferences
                        val inicio = jobl.first().horainicio
                        val fin = jobl.first().horafin
                        preferences.edit()
                            .putString(KEY_HORA_INICIO, inicio)
                            .putString(KEY_HORA_FIN, fin)
                            .apply()

                        setProgressAsync(workDataOf("estado" to "Configuracion almacenada"))
                        kotlinx.coroutines.delay(300)
                    }

                    is ResultadoApi.ErrorHttp -> {
                        setProgressAsync(workDataOf("estado" to "Error HTTP ${resultado.code}"))
                        throw Exception("Error HTTP ${resultado.code}: ${resultado.mensaje}")
                    }

                    is ResultadoApi.Fallo -> {
                        setProgressAsync(workDataOf("estado" to "Fallo: ${resultado.mensaje}"))
                        throw Exception("Fallo: ${resultado.mensaje}")
                    }
                }
            }

            Result.success(workDataOf("resultado" to "OK"))
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to (e.message ?: "Error desconocido")))
        }
    }
}