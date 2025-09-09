package com.upd.kvupd.data.remote

import com.upd.kvupd.data.model.JsonResponseAny
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class UploadSource @Inject constructor(
    private val apiBuilder: ApiBuilder
) {

    // Creacion del Api para consultas
    private suspend fun api() = apiBuilder.createAPI()

    // ENVIAR DATOS AL SERVIDOR
    suspend fun sendRegistro(body: RequestBody): Response<JsonResponseAny> =
        api().sendRegistro(body)

    suspend fun sendSeguimiento(body: RequestBody): Response<JsonResponseAny> =
        api().sendSeguimiento(body)

    suspend fun sendVisita(body: RequestBody): Response<JsonResponseAny> =
        api().sendVisita(body)

    suspend fun sendAlta(body: RequestBody): Response<JsonResponseAny> =
        api().sendAlta(body)

    suspend fun sendAltaDetalle(body: RequestBody): Response<JsonResponseAny> =
        api().sendAltaDetalle(body)

    suspend fun sendAltaFoto(body: RequestBody): Response<JsonResponseAny> =
        api().sendAltaFoto(body)

    suspend fun sendBaja(body: RequestBody): Response<JsonResponseAny> =
        api().sendBaja(body)

    suspend fun sendConfirmarBaja(body: RequestBody): Response<JsonResponseAny> =
        api().sendConfirmarBaja(body)

    suspend fun sendRespuesta(body: RequestBody): Response<JsonResponseAny> =
        api().sendRespuesta(body)

    suspend fun sendFoto(body: RequestBody): Response<JsonResponseAny> =
        api().sendFoto(body)
}