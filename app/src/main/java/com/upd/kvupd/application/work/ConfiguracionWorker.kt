package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ConfiguracionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val generalFunctions: IdentityFunctions,
    private val roomFunctions: RoomFunctions,
    private val serverFunctions: ServerFunctions,
    private val jsobFunctions: JsObFunctions
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val uuid = generalFunctions.obtenerIdentificador()
                ?: return Result.failure(workDataOf("error" to "No se encontro UUID valido"))

            val json = jsobFunctions.jsonObjectConfiguracion(uuid)
            serverFunctions.apiDownloadConfiguracion(json).first { resultado ->
                when (resultado) {
                    is ResultadoApi.Loading -> {
                        setProgressAsync(workDataOf("estado" to "Descargando configuración..."))
                        false // seguimos escuchando
                    }

                    is ResultadoApi.Exito -> {
                        val jobl = resultado.data?.jobl
                            ?: throw Exception("No se encontro configuracion")

                        setProgressAsync(workDataOf("estado" to "Guardando configuración"))
                        roomFunctions.deleteConfiguracion()
                        roomFunctions.apiSaveConfiguracion(jobl)
                        setProgressAsync(workDataOf("estado" to "Configuración almacenada"))
                        true // cortamos con first
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