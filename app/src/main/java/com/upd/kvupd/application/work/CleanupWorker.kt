package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.utils.FechaHoraUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val roomFunctions: RoomFunctions
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {

            val hoy = FechaHoraUtil.dia() // "yyyy-MM-dd"

            // 🔥 Validar si hay data para limpiar
            val hayData = roomFunctions.needDatosLimpieza(hoy)

            if (!hayData) return Result.success()

            // 🔥 1. Obtener rutas de fotos a eliminar
            val rutas = roomFunctions.queryListaRutasFoto(hoy)

            // 🔥 2. Eliminar archivos físicos
            rutas.forEach { ruta ->
                if (ruta.isNotBlank()) {
                    val file = File(ruta)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }

            // 🔥 3. Eliminar datos en DB (transacción)
            roomFunctions.clearServerUploadData(hoy)

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}