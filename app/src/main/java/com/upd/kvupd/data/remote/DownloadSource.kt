package com.upd.kvupd.data.remote

import com.upd.kvupd.data.model.JsonBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaVendedor
import com.upd.kvupd.data.model.JsonCambio
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.JsonCoberturaCartera
import com.upd.kvupd.data.model.JsonCoberturados
import com.upd.kvupd.data.model.JsonConfiguracion
import com.upd.kvupd.data.model.JsonConsulta
import com.upd.kvupd.data.model.JsonDetalleCobertura
import com.upd.kvupd.data.model.JsonDistrito
import com.upd.kvupd.data.model.JsonEncuesta
import com.upd.kvupd.data.model.JsonGenerico
import com.upd.kvupd.data.model.JsonNegocio
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonPedidoGeneral
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.JsonRuta
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVendedor
import com.upd.kvupd.data.model.JsonVolumen
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class DownloadSource @Inject constructor(
    private val apiBuilder: ApiBuilder
) {

    // Creacion del Api para consultas
    private suspend fun api() = apiBuilder.createAPI()

    // DESCARGA INICIAL DE DATOS
    suspend fun downloadConfiguracion(body: RequestBody): Response<JsonConfiguracion> =
        api().downloadConfiguracion(body)

    suspend fun downloadCliente(body: RequestBody): Response<JsonCliente> =
        api().downloadCliente(body)

    suspend fun downloadEmpleado(body: RequestBody): Response<JsonVendedor> =
        api().downloadEmpleado(body)

    suspend fun downloadRuta(body: RequestBody): Response<JsonRuta> =
        api().downloadRuta(body)

    suspend fun downloadDistrito(body: RequestBody): Response<JsonDistrito> =
        api().downloadDistrito(body)

    suspend fun downloadNegocio(body: RequestBody): Response<JsonNegocio> =
        api().downloadNegocio(body)

    suspend fun downloadEncuesta(body: RequestBody): Response<JsonEncuesta> =
        api().downloadEncuesta(body)


    // CONSULTA DE DATOS EN SERVIDOR
    suspend fun queryConsulta(body: RequestBody): Response<JsonConsulta> =
        api().queryConsulta(body)

    suspend fun queryPedimap(body: RequestBody): Response<JsonPedimap> =
        api().queryPedimap(body)

    suspend fun querySupervisorBajas(body: RequestBody): Response<JsonBajaSupervisor> =
        api().querySupervisorBajas(body)

    suspend fun queryVendedorBajas(body: RequestBody): Response<JsonBajaVendedor> =
        api().queryVendedorBajas(body)


    // SOLICITAR DATOS DE REPORTE
    suspend fun reportPreventa(body: RequestBody): Response<JsonVolumen> =
        api().reportPreventa(body)

    suspend fun reportCobertura(body: RequestBody): Response<JsonCoberturaCartera> =
        api().reportCobertura(body)

    suspend fun reportCoberturaDetalle(body: RequestBody): Response<JsonDetalleCobertura> =
        api().reportCoberturaDetalle(body)

    suspend fun reportCartera(body: RequestBody): Response<JsonCoberturaCartera> =
        api().reportCartera(body)

    suspend fun reportGeneral(body: RequestBody): Response<JsonPedido> =
        api().reportGeneral(body)

    suspend fun reportClienteCambio(body: RequestBody): Response<JsonCambio> =
        api().reportClienteCambio(body)

    suspend fun reportEmpleadoCambio(body: RequestBody): Response<JsonCambio> =
        api().reportEmpleadoCambio(body)

    suspend fun reportSoles(body: RequestBody): Response<JsonSoles> =
        api().reportSoles(body)

    suspend fun reportSolesGenerico(body: RequestBody): Response<JsonGenerico> =
        api().reportSolesGenerico(body)

    suspend fun reportCoberturaPendiente(body: RequestBody): Response<JsonCoberturados> =
        api().reportCoberturaPendiente(body)

    suspend fun reportEmpleado(body: RequestBody): Response<JsonPedidoGeneral> =
        api().reportEmpleado(body)
}