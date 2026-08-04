package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.core.util.AtomicFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.upd.kvupd.application.work.processor.CoreCsvProcessor
import com.upd.kvupd.application.work.processor.enumFile.CsvSendResult
import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.utils.BaseDatosRoom.FOLDER_CORE
import com.upd.kvupd.utils.ConstantsExtras.NO_FIND_UUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class CoreCsvWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val processor: CoreCsvProcessor,
    private val identityFunctions: IdentityFunctions
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val folder = File(
            applicationContext.filesDir,
            FOLDER_CORE
        )

        if (!folder.exists()) {
            return Result.success()
        }

        val files = folder.listFiles()
            ?.filter { it.extension == "csv" }
            ?.sortedBy { it.name }
            .orEmpty()

        val extraParam = identityFunctions.obtenerIdentificador()
            .takeUnless { it.isNullOrBlank() }
            ?: NO_FIND_UUID

        var necesitaRetry = false

        for (file in files) {

            val result = try {
                when {
                    file.name.contains("seguimiento") ->
                        processor.procesarSeguimiento(file, extraParam)

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
                        descartarCsvDesconocido(file)
                }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {
                CsvSendResult.RETRY
            }

            when (result) {
                CsvSendResult.RETRY -> {
                    if (registrarReintento(file)) {
                        necesitaRetry = true
                    } else {
                        descartarPorReintentosAgotados(file)
                    }
                }

                CsvSendResult.SUCCESS,
                CsvSendResult.DISCARD -> {
                    eliminarMarcaReintento(file)
                }
            }
        }

        return if (necesitaRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    private fun registrarReintento(file: File): Boolean {
        val retryFile = retryFile(file)

        val reintentosActuales = if (!retryFile.exists()) {
            0
        } else {
            runCatching {
                retryFile.readText()
                    .trim()
                    .toIntOrNull()
            }.getOrNull()
                ?: MAX_REINTENTOS_ADICIONALES
        }

        if (reintentosActuales >= MAX_REINTENTOS_ADICIONALES) {
            return false
        }

        val nuevoValor = reintentosActuales + 1

        return escribirContadorAtomico(
            file = retryFile,
            value = nuevoValor
        )
    }

    private fun escribirContadorAtomico(
        file: File,
        value: Int
    ): Boolean {
        val atomicFile = AtomicFile(file)
        var output: FileOutputStream? = null

        return try {
            output = atomicFile.startWrite()
            output.write(
                value.toString().toByteArray(Charsets.UTF_8)
            )

            atomicFile.finishWrite(output)
            output = null

            true

        } catch (error: Exception) {
            output?.let(atomicFile::failWrite)

            Log.e(
                CoreCsvWorker::class.java.simpleName,
                "No se pudo guardar el contador de ${file.name}",
                error
            )

            false
        }
    }

    private fun descartarPorReintentosAgotados(file: File) {
        val report = IllegalStateException(
            "CSV descartado después de " +
                    "$MAX_REINTENTOS_ADICIONALES reintentos: ${file.name}"
        )

        Log.e(
            CoreCsvWorker::class.java.simpleName,
            report.message,
            report
        )

        runCatching {
            FirebaseCrashlytics.getInstance()
                .recordException(report)
        }

        val eliminado = !file.exists() || file.delete()

        if (eliminado) {
            eliminarMarcaReintento(file)
            return
        }

        val quarantinedFile = File(
            file.parentFile,
            "${file.name}.failed"
        )

        if (file.renameTo(quarantinedFile)) {
            eliminarMarcaReintento(file)
        }
    }

    private fun descartarCsvDesconocido(
        file: File
    ): CsvSendResult {
        val report = IllegalArgumentException(
            "Tipo de CSV no reconocido: ${file.name}"
        )

        Log.e(
            CoreCsvWorker::class.java.simpleName,
            report.message,
            report
        )

        runCatching {
            FirebaseCrashlytics.getInstance()
                .recordException(report)
        }

        return if (!file.exists() || file.delete()) {
            CsvSendResult.DISCARD
        } else {
            CsvSendResult.RETRY
        }
    }

    private fun retryFile(file: File): File =
        File(
            file.parentFile,
            "${file.name}.retry"
        )

    private fun eliminarMarcaReintento(file: File) {
        AtomicFile(retryFile(file)).delete()
    }

    private companion object {
        const val MAX_REINTENTOS_ADICIONALES = 3
    }
}