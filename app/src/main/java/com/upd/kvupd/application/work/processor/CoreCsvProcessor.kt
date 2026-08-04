package com.upd.kvupd.application.work.processor

import android.util.Log
import androidx.core.util.AtomicFile
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.upd.kvupd.application.work.processor.enumFile.CsvSendResult
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import com.upd.kvupd.domain.send.SendServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.BaseDatosRoom.SEPARADOR
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CoreCsvProcessor @Inject constructor(
    private val sendServerFunctions: SendServerFunctions
) {

    suspend fun procesarSeguimiento(file: File, uuid: String): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseSeguimiento,
            sender = {
                sendServerFunctions.enviarSeguimiento(
                    it,
                    uuid
                )
            }
        )

    suspend fun procesarAlta(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseAlta,
            sender = { sendServerFunctions.enviarAlta(it) }
        )

    suspend fun procesarAltaDatos(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseAltaDatos,
            sender = { sendServerFunctions.enviarAltaDatos(it) }
        )

    suspend fun procesarBaja(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseBaja,
            sender = { sendServerFunctions.enviarBaja(it) }
        )

    suspend fun procesarBajaProcesada(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseBajaProcesada,
            sender = { sendServerFunctions.enviarBajaProcesada(it) }
        )

    suspend fun procesarFoto(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseFoto,
            sender = { sendServerFunctions.enviarFoto(it) }
        )

    suspend fun procesarRespuesta(file: File): CsvSendResult =
        procesarIndividual(
            file = file,
            parser = ::parseRespuesta,
            sender = { sendServerFunctions.enviarRespuesta(it) }
        )

    private suspend fun <T> procesarIndividual(
        file: File,
        parser: (String) -> T,
        sender: suspend (T) -> ResultadoApi<Unit>
    ): CsvSendResult {

        val lineas = try {
            file.readLines(Charsets.UTF_8)
        } catch (error: Exception) {
            // Un fallo de lectura podría ser temporal.
            return CsvSendResult.RETRY
        }

        if (lineas.size <= 1) {
            return finalizarArchivo(
                file = file,
                resultado = CsvSendResult.SUCCESS
            )
        }

        val encabezado = lineas.first()
        val filas = lineas.drop(1)

        val elementos = try {
            filas.map { linea ->
                linea to parser(linea)
            }
        } catch (error: Exception) {
            return descartarCsvCorrupto(file, error)
        }

        return try {
            for (indice in elementos.indices) {
                val (_, item) = elementos[indice]

                val resultado = try {
                    sender(item).toCsvResult()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    CsvSendResult.RETRY
                }

                when (resultado) {
                    CsvSendResult.SUCCESS,
                    CsvSendResult.DISCARD -> Unit

                    CsvSendResult.RETRY -> {
                        val pendientes = elementos
                            .drop(indice)
                            .map { it.first }

                        reescribirCsv(
                            file = file,
                            encabezado = encabezado,
                            filas = pendientes
                        )

                        return CsvSendResult.RETRY
                    }
                }
            }

            finalizarArchivo(
                file = file,
                resultado = CsvSendResult.SUCCESS
            )

        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            CsvSendResult.RETRY
        }
    }

    private fun descartarCsvCorrupto(
        file: File,
        cause: Exception
    ): CsvSendResult {

        val report = IllegalArgumentException(
            "CSV corrupto o incompatible: ${file.name}",
            cause
        )

        Log.e(
            CoreCsvProcessor::class.java.simpleName,
            report.message,
            report
        )

        runCatching {
            FirebaseCrashlytics.getInstance().recordException(report)
        }

        if (!file.exists() || file.delete()) {
            return CsvSendResult.DISCARD
        }

        /*
         * Si no se puede eliminar, se cambia la extensión para impedir
         * que el Worker vuelva a procesarlo como CSV.
         */
        val quarantinedFile = File(
            file.parentFile,
            "${file.name}.invalid"
        )

        return if (file.renameTo(quarantinedFile)) {
            CsvSendResult.DISCARD
        } else {
            CsvSendResult.RETRY
        }
    }

    private fun reescribirCsv(
        file: File,
        encabezado: String,
        filas: List<String>
    ) {
        val atomicFile = AtomicFile(file)
        var output: FileOutputStream? = null

        try {
            output = atomicFile.startWrite()

            val writer = OutputStreamWriter(
                output,
                Charsets.UTF_8
            ).buffered()

            writer.appendLine(encabezado)

            filas.forEach { fila ->
                writer.appendLine(fila)
            }

            writer.flush()
            atomicFile.finishWrite(output)

        } catch (e: Exception) {
            output?.let(atomicFile::failWrite)
            throw e
        }
    }

    private fun ResultadoApi<Unit>.toCsvResult(): CsvSendResult =
        when (this) {

            is ResultadoApi.Exito ->
                CsvSendResult.SUCCESS

            is ResultadoApi.Fallo ->
                CsvSendResult.RETRY

            is ResultadoApi.ErrorHttp ->
                CsvSendResult.DISCARD

            is ResultadoApi.Loading ->
                CsvSendResult.RETRY
        }

    private fun finalizarArchivo(
        file: File,
        resultado: CsvSendResult
    ): CsvSendResult {

        val eliminado = !file.exists() || file.delete()

        return if (eliminado) {
            resultado
        } else {
            CsvSendResult.RETRY
        }
    }

    private fun parseSeguimiento(linea: String): TableSeguimiento {
        val v = csv(linea)
        require(v.size >= 6)

        return TableSeguimiento(
            fecha = v[0],
            usuario = v[1],
            longitud = v[2].toDouble(),
            latitud = v[3].toDouble(),
            precision = v[4].toDouble(),
            bateria = v[5].toDouble(),
            sincronizado = false
        )
    }

    private fun parseAlta(linea: String): TableAlta {
        val v = csv(linea)
        require(v.size >= 7)

        return TableAlta(
            idaux = v[0],
            empleado = v[1],
            fecha = v[2],
            longitud = v[3].toDouble(),
            latitud = v[4].toDouble(),
            precision = v[5].toDouble(),
            datos = v[6].toInt(),
            sincronizado = false
        )
    }

    private fun parseAltaDatos(linea: String): TableAltaDatos {
        val v = csv(linea)
        require(v.size >= 26)

        return TableAltaDatos(
            fecha = v[0],
            idaux = v[1],
            empleado = v[2],
            tipo = v[3],
            razon = v[4],
            nombre = v[5],
            appaterno = v[6],
            apmaterno = v[7],
            ruc = v[8],
            dnice = v[9],
            tipodocu = v[10],
            movil1 = v[11],
            movil2 = v[12],
            correo = v[13],
            via = v[14],
            direccion = v[15],
            manzana = v[16],
            zona = v[17],
            zonanombre = v[18],
            ubicacion = v[19],
            numero = v[20],
            distrito = v[21],
            giro = v[22],
            ruta = v[23],
            secuencia = v[24],
            observacion = v[25],
            sincronizado = false
        )
    }

    private fun parseBaja(linea: String): TableBaja {
        val v = csv(linea)
        require(v.size >= 9)

        return TableBaja(
            cliente = v[0],
            nombre = v[1],
            motivo = v[2].toInt(),
            comentario = v[3],
            longitud = v[4].toDouble(),
            latitud = v[5].toDouble(),
            precision = v[6].toDouble(),
            fecha = v[7],
            anulado = v[8].toInt(),
            sincronizado = false
        )
    }

    private fun parseBajaProcesada(linea: String): TableBajaProcesada {
        val v = csv(linea)
        require(v.size >= 9)

        return TableBajaProcesada(
            empleado = v[0],
            cliente = v[1],
            procede = v[2].toInt(),
            fecha = v[3],
            precision = v[4].toDouble(),
            longitud = v[5].toDouble(),
            latitud = v[6].toDouble(),
            fechaconfirmacion = v[7],
            observacion = v[8],
            sincronizado = false
        )
    }

    private fun parseRespuesta(linea: String): TableRespuesta {
        val v = csv(linea)
        require(v.size >= 7)

        return TableRespuesta(
            cliente = v[0],
            fecha = v[1],
            encuesta = v[2].toInt(),
            pregunta = v[3].toInt(),
            respuesta = v[4],
            longitud = v[5].toDouble(),
            latitud = v[6].toDouble(),
            sincronizado = false
        )
    }

    private fun parseFoto(linea: String): TableFoto {
        val v = csv(linea)
        require(v.size >= 4)

        return TableFoto(
            cliente = v[0],
            fecha = v[1],
            encuesta = v[2].toInt(),
            rutafoto = v[3],
            sincronizado = false
        )
    }

    private fun csv(linea: String): List<String> {

        val separator = SEPARADOR.single()
        val resultado = mutableListOf<String>()
        val actual = StringBuilder()

        var enComillas = false
        var i = 0

        while (i < linea.length) {
            val c = linea[i]

            when {

                c == '"' -> {
                    if (
                        enComillas &&
                        i + 1 < linea.length &&
                        linea[i + 1] == '"'
                    ) {
                        actual.append('"')
                        i++
                    } else {
                        enComillas = !enComillas
                    }
                }

                c == separator && !enComillas -> {
                    resultado.add(actual.toString().trim())
                    actual.clear()
                }

                else -> actual.append(c)
            }
            i++
        }

        require(!enComillas) {
            "Campo CSV con comillas sin cerrar"
        }

        resultado.add(actual.toString().trim())
        return resultado
    }
}