package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.application.work.processor.CoreCsvProcessor
import com.upd.kvupd.application.work.processor.enumFile.CsvSendResult
import com.upd.kvupd.utils.BaseDatosRoom.FOLDER_CORE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class CoreCsvWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val processor: CoreCsvProcessor
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val folder = File(
            applicationContext.filesDir,
            FOLDER_CORE
        )

        if (!folder.exists()) return Result.success()

        val files = folder.listFiles()
            ?.filter { it.extension == "csv" }
            ?.sortedBy { it.name }
            .orEmpty()

        var necesitaRetry = false

        files.forEach { file ->

            val result = runCatching {

                when {

                    file.name.contains("seguimiento") ->
                        processor.procesarSeguimiento(file)

                    file.name.contains("altadatos") ->
                        processor.procesarAltaDatos(file)

                    file.name.contains("alta") ->
                        processor.procesarAlta(file)

                    file.name.contains("bajaprocesada") ->
                        processor.procesarBajaProcesada(file)

                    file.name.contains("baja") ->
                        processor.procesarBaja(file)

                    file.name.contains("respuesta") ->
                        processor.procesarRespuesta(file)

                    file.name.contains("foto") ->
                        processor.procesarFoto(file)

                    else ->
                        CsvSendResult.DISCARD
                }

            }.getOrElse {
                CsvSendResult.RETRY
            }

            if (result == CsvSendResult.RETRY) {
                necesitaRetry = true
            }
        }

        return if (necesitaRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}