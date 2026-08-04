package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.enumFile.TipoUsuario
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class CacheRecoveryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val roomFunctions: RoomFunctions,
    private val operationsFunctions: OperationsFunctions
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val configuracion = roomFunctions.queryConfiguracion()
                ?: return Result.failure(
                    workDataOf(
                        "error" to "No se encontró configuración actualizada"
                    )
                )

            val tipoUsuario = TipoUsuario.fromCodigo(configuracion.tipo)

            val workerIds = operationsFunctions.remainingWorkers(tipoUsuario)

            if (workerIds.isEmpty()) {
                Result.failure(
                    workDataOf(
                        "error" to
                                "No se pudieron programar los Workers de CacheRoom"
                    )
                )
            } else {
                Result.success(
                    workDataOf(
                        "resultado" to
                                "Recuperación de CacheRoom programada"
                    )
                )
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(
                workDataOf(
                    "error" to (
                            error.message
                                ?: "Error recuperando CacheRoom"
                            )
                )
            )
        }
    }
}