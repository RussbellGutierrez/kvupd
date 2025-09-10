package com.upd.kvupd.domain

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
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.data.model.JsonRuta
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVendedor
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.ui.sealed.ResultadoApi
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface ServerFunctions {
    fun apiDownloadConfiguracion(body: RequestBody): Flow<ResultadoApi<JsonConfiguracion>>
    fun apiDownloadCliente(body: RequestBody): Flow<ResultadoApi<JsonCliente>>
    fun apiDownloadEmpleado(body: RequestBody): Flow<ResultadoApi<JsonVendedor>>
    fun apiDownloadRuta(body: RequestBody): Flow<ResultadoApi<JsonRuta>>
    fun apiDownloadDistrito(body: RequestBody): Flow<ResultadoApi<JsonDistrito>>
    fun apiDownloadNegocio(body: RequestBody): Flow<ResultadoApi<JsonNegocio>>
    fun apiDownloadEncuesta(body: RequestBody): Flow<ResultadoApi<JsonEncuesta>>

    fun apiQueryConsulta(body: RequestBody): Flow<ResultadoApi<JsonConsulta>>
    fun apiQueryPedimap(body: RequestBody): Flow<ResultadoApi<JsonPedimap>>
    fun apiQuerySupervisorBajas(body: RequestBody): Flow<ResultadoApi<JsonBajaSupervisor>>
    fun apiQueryVendedorBajas(body: RequestBody): Flow<ResultadoApi<JsonBajaVendedor>>

    fun apiReportPreventa(body: RequestBody): Flow<ResultadoApi<JsonVolumen>>
    fun apiReportCobertura(body: RequestBody): Flow<ResultadoApi<JsonCoberturaCartera>>
    fun apiReportCoberturaDetalle(body: RequestBody): Flow<ResultadoApi<JsonDetalleCobertura>>
    fun apiReportCartera(body: RequestBody): Flow<ResultadoApi<JsonCoberturaCartera>>
    fun apiReportGeneral(body: RequestBody): Flow<ResultadoApi<JsonPedido>>
    fun apiReportClienteCambio(body: RequestBody): Flow<ResultadoApi<JsonCambio>>
    fun apiReportEmpleadoCambio(body: RequestBody): Flow<ResultadoApi<JsonCambio>>
    fun apiReportSoles(body: RequestBody): Flow<ResultadoApi<JsonSoles>>
    fun apiReportSolesGenerico(body: RequestBody): Flow<ResultadoApi<JsonGenerico>>
    fun apiReportCoberturaPendiente(body: RequestBody): Flow<ResultadoApi<JsonCoberturados>>
    fun apiReportEmpleado(body: RequestBody): Flow<ResultadoApi<JsonPedidoGeneral>>

    fun apiSendRegistro(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendSeguimiento(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendVisita(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendAlta(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendAltaDetalle(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendAltaFoto(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendBaja(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendConfirmarBaja(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendRespuesta(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
    fun apiSendFoto(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>>
}