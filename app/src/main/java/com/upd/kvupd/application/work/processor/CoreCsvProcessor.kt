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
        parser: (Map<String, String>) -> T,
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
            val columnas = parseEncabezado(encabezado)

            filas.map { linea ->
                val valores = parseFila(
                    columnas = columnas,
                    linea = linea
                )

                linea to parser(valores)
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

    private fun parseEncabezado(
        encabezado: String
    ): List<String> {
        val columnas = csv(encabezado)
            .map(String::trim)

        require(columnas.isNotEmpty()) {
            "El CSV no contiene encabezados"
        }

        require(columnas.none(String::isBlank)) {
            "El CSV contiene encabezados vacíos"
        }

        require(columnas.distinct().size == columnas.size) {
            "El CSV contiene encabezados duplicados"
        }

        return columnas
    }

    private fun parseFila(
        columnas: List<String>,
        linea: String
    ): Map<String, String> {
        val valores = csv(linea)

        require(valores.size == columnas.size) {
            "La fila contiene ${valores.size} valores, " +
                    "pero el encabezado define ${columnas.size} columnas"
        }

        return columnas.zip(valores).toMap()
    }

    private fun Map<String, String>.valor(
        columna: String
    ): String =
        get(columna)
            ?: throw IllegalArgumentException(
                "Falta la columna obligatoria: $columna"
            )

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

            is ResultadoApi.Exito -> CsvSendResult.SUCCESS

            is ResultadoApi.Fallo -> CsvSendResult.RETRY

            is ResultadoApi.ErrorHttp -> CsvSendResult.DISCARD

            is ResultadoApi.Loading -> CsvSendResult.RETRY
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

    private fun parseSeguimiento(
        v: Map<String, String>
    ): TableSeguimiento =
        TableSeguimiento(
            fecha =  v.valor("fecha"),
            usuario = v.valor("usuario"),
            longitud = v.valor("longitud").toDouble(),
            latitud = v.valor("latitud").toDouble(),
            precision = v.valor("precision").toDouble(),
            bateria = v.valor("bateria").toDouble(),
            sincronizado = false
        )

    private fun parseAlta(
        v: Map<String, String>
    ): TableAlta =
        TableAlta(
            idaux = v.valor("idaux"),
            empleado = v.valor("empleado"),
            fecha = v.valor("fecha"),
            longitud = v.valor("longitud").toDouble(),
            latitud = v.valor("latitud").toDouble(),
            precision = v.valor("precision").toDouble(),
            datos = v.valor("datos").toInt(),
            sincronizado = false
        )

    private fun parseAltaDatos(
        v: Map<String, String>
    ): TableAltaDatos =
        TableAltaDatos(
            fecha = v.valor("fecha"),
            idaux = v.valor("idaux"),
            empleado = v.valor("empleado"),
            tipo = v.valor("tipo"),
            razon = v.valor("razon"),
            nombre = v.valor("nombre"),
            appaterno = v.valor("appaterno"),
            apmaterno = v.valor("apmaterno"),
            ruc = v.valor("ruc"),
            dnice = v.valor("dnice"),
            tipodocu = v.valor("tipodocu"),
            movil1 = v.valor("movil1"),
            movil2 = v.valor("movil2"),
            correo = v.valor("correo"),
            via = v.valor("via"),
            direccion = v.valor("direccion"),
            manzana = v.valor("manzana"),
            zona = v.valor("zona"),
            zonanombre = v.valor("zonanombre"),
            ubicacion = v.valor("ubicacion"),
            numero = v.valor("numero"),
            distrito = v.valor("distrito"),
            giro = v.valor("giro"),
            ruta = v.valor("ruta"),
            secuencia = v.valor("secuencia"),
            observacion = v.valor("observacion"),
            sincronizado = false
        )

    private fun parseBaja(
        v: Map<String, String>
    ): TableBaja =
        TableBaja(
            cliente = v.valor("cliente"),
            nombre = v.valor("nombre"),
            motivo = v.valor("motivo").toInt(),
            comentario = v.valor("comentario"),
            longitud = v.valor("longitud").toDouble(),
            latitud = v.valor("latitud").toDouble(),
            precision = v.valor("precision").toDouble(),
            fecha = v.valor("fecha"),
            anulado = v.valor("anulado").toInt(),
            sincronizado = false
        )

    private fun parseBajaProcesada(
        v: Map<String, String>
    ): TableBajaProcesada =
        TableBajaProcesada(
            empleado = v.valor("empleado"),
            cliente = v.valor("cliente"),
            procede = v.valor("procede").toInt(),
            fecha = v.valor("fecha"),
            precision = v.valor("precision").toDouble(),
            longitud = v.valor("longitud").toDouble(),
            latitud = v.valor("latitud").toDouble(),
            fechaconfirmacion = v.valor("fechaconfirmacion"),
            observacion = v.valor("observacion"),
            sincronizado = false
        )

    private fun parseRespuesta(
        v: Map<String, String>
    ): TableRespuesta =
        TableRespuesta(
            cliente = v.valor("cliente"),
            fecha = v.valor("fecha"),
            encuesta = v.valor("encuesta").toInt(),
            pregunta = v.valor("pregunta").toInt(),
            respuesta = v.valor("respuesta"),
            longitud = v.valor("longitud").toDouble(),
            latitud = v.valor("latitud").toDouble(),
            sincronizado = false
        )

    private fun parseFoto(
        v: Map<String, String>
    ): TableFoto =
        TableFoto(
            cliente = v.valor("cliente"),
            fecha = v.valor("fecha"),
            encuesta = v.valor("encuesta").toInt(),
            rutafoto = v.valor("rutafoto"),
            sincronizado = false
        )

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