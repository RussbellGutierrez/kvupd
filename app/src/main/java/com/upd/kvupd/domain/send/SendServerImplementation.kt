package com.upd.kvupd.domain.send

import android.content.Context
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import javax.inject.Inject

class SendServerImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
    private val room: RoomFunctions,
    private val server: ServerFunctions,
    private val json: JsObFunctions
) : SendServerFunctions {

    override suspend fun enviarBaja(item: TableBaja): ResultadoApi<Unit> =
        ejecutarEnvio(
            item,
            buildBody = { config, it -> json.jsonObjectBajas(config, it) },
            send = { server.apiSendBaja(it) },
            onSuccess = { room.updateBaja(it.copy(sincronizado = true)) },
            onError = { room.updateBaja(it.copy(sincronizado = false)) }
        )

    override suspend fun enviarBajaProcesada(item: TableBajaProcesada): ResultadoApi<Unit> =
        ejecutarEnvio(
            item,
            buildBody = { config, it -> json.jsonObjectBajasProcesadas(config, it) },
            send = { server.apiSendConfirmarBaja(it) },
            onSuccess = { room.updateBajaProcesada(it.copy(sincronizado = true)) },
            onError = { room.updateBajaProcesada(it.copy(sincronizado = false)) }
        )

    override suspend fun enviarAlta(item: TableAlta): ResultadoApi<Unit> =
        ejecutarEnvio(
            item,
            buildBody = { config, it -> json.jsonObjectAltas(config, it) },
            send = { server.apiSendAlta(it) },
            onSuccess = { room.updateAlta(it.copy(sincronizado = true)) },
            onError = { room.updateAlta(it.copy(sincronizado = false)) }
        )

    override suspend fun enviarAltaDatos(item: TableAltaDatos): ResultadoApi<Unit> =
        ejecutarEnvio(
            item,
            buildBody = { config, it -> json.jsonObjectAltaDatos(config, it) },
            send = { server.apiSendAltaDetalle(it) },
            onSuccess = { room.updateDatosAlta(it.copy(sincronizado = true)) },
            onError = { room.updateDatosAlta(it.copy(sincronizado = false)) }
        )

    override suspend fun enviarRespuesta(item: List<TableRespuesta>): ResultadoApi<Unit> {

        var primerError: ResultadoApi<Nothing>? = null

        item.forEach { respuesta ->
            val result = ejecutarEnvio(
                respuesta,
                buildBody = { config, it -> json.jsonObjectRespuesta(config, it) },
                send = { server.apiSendRespuesta(it) },
                onSuccess = { room.updateRespuesta(it.copy(sincronizado = true)) },
                onError = { room.updateRespuesta(it.copy(sincronizado = false)) }
            )

            if (primerError == null) {
                when (result) {
                    is ResultadoApi.ErrorHttp -> primerError = result
                    is ResultadoApi.Fallo -> primerError = result
                    else -> {}
                }
            }
        }
        return primerError ?: ResultadoApi.Exito(Unit)
    }

    override suspend fun enviarFoto(item: TableFoto): ResultadoApi<Unit> =
        ejecutarEnvio(
            item,
            buildBody = { config, it -> json.jsonObjectFoto(config, it) },
            send = { server.apiSendFoto(it) },
            onSuccess = { room.updateFoto(it.copy(sincronizado = true)) },
            onError = { room.updateFoto(it.copy(sincronizado = false)) }
        )

    private suspend fun <T> ejecutarEnvio(
        item: T,
        buildBody: (config: TableConfiguracion, item: T) -> RequestBody,
        send: (RequestBody) -> Flow<ResultadoApi<*>>,
        onSuccess: suspend (T) -> Unit,
        onError: suspend (T) -> Unit
    ): ResultadoApi<Unit> {

        val config = room.queryConfiguracion()
            ?: return ResultadoApi.Fallo(IllegalStateException("Sin configuración"))

        val body = buildBody(config, item)
        var resultadoFinal: ResultadoApi<Unit> = ResultadoApi.Loading

        send(body).collect { result ->
            resultadoFinal = when (result) {

                is ResultadoApi.Exito -> {
                    onSuccess(item)
                    ResultadoApi.Exito(Unit)
                }

                is ResultadoApi.ErrorHttp -> {
                    onError(item)
                    result
                }

                is ResultadoApi.Fallo -> {
                    onError(item)
                    result
                }

                ResultadoApi.Loading -> ResultadoApi.Loading
            }
        }

        return resultadoFinal
    }
}