package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EncuestasWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val roomFunctions: RoomFunctions,
    private val serverFunctions: ServerFunctions,
    private val jsobFunctions: JsObFunctions
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val config = roomFunctions.queryConfiguracion()
                ?: return Result.failure(workDataOf("error" to "No se encontro configuracion"))

            val json = jsobFunctions.jsonObjectBasico(config)
            serverFunctions.apiDownloadEncuesta(json).collect { resultado ->
                when (resultado) {
                    is ResultadoApi.Loading -> {
                        setProgressAsync(workDataOf("estado" to "Iniciando descarga encuestas..."))
                        kotlinx.coroutines.delay(300)
                    }

                    is ResultadoApi.Exito -> {
                        val jobl = resultado.data?.jobl ?: emptyList()

                        setProgressAsync(workDataOf("estado" to "Almacenando encuestas"))
                        kotlinx.coroutines.delay(300)

                        roomFunctions.replaceEncuesta(jobl)

                        setProgressAsync(workDataOf("estado" to "Registros de encuestas: ${jobl.size}"))
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