package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.upload.UploadManager
import com.upd.kvupd.utils.ConstantsExtras.NO_FIND_UUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ServidorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val uploadManager: UploadManager,
    private val roomFunctions: RoomFunctions,
    private val identityFunctions: IdentityFunctions
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        // 🔹 Validar configuración
        roomFunctions.queryConfiguracion()
            ?: return Result.success()

        // 🔹 🔥 Validar si hay pendientes
        val hayPendientes = roomFunctions.existDatosPendientes()

        if (!hayPendientes) return Result.success()

        // 🔹 Obtener UUID
        val extraParam = identityFunctions.obtenerIdentificador()
            .takeUnless { it.isNullOrBlank() }
            ?: NO_FIND_UUID

        return try {

            uploadManager.uploadAll(extraParam)

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}