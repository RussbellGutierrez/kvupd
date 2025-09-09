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
import com.upd.kvupd.data.model.JsonPedidoGeneral
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.JsonRuta
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVendedor
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.data.model.Login
import com.upd.kvupd.data.remote.DownloadSource
import com.upd.kvupd.data.remote.UploadSource
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.remoteFlowCall
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import javax.inject.Inject

class ServerImplementation @Inject constructor(
    private val downloadSource: DownloadSource,
    private val uploadSource: UploadSource
) : ServerFunctions {

    override fun apiDownloadConfiguracion(body: RequestBody): Flow<ResultadoApi<JsonConfiguracion>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadConfiguracion(body) }
        )

    override fun apiDownloadCliente(body: RequestBody): Flow<ResultadoApi<JsonCliente>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadCliente(body) }
        )

    override fun apiDownloadEmpleado(body: RequestBody): Flow<ResultadoApi<JsonVendedor>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadEmpleado(body) }
        )

    override fun apiDownloadRuta(body: RequestBody): Flow<ResultadoApi<JsonRuta>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadRuta(body) }
        )

    override fun apiDownloadDistrito(body: RequestBody): Flow<ResultadoApi<JsonDistrito>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadDistrito(body) }
        )

    override fun apiDownloadNegocio(body: RequestBody): Flow<ResultadoApi<JsonNegocio>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadNegocio(body) }
        )

    override fun apiDownloadEncuesta(body: RequestBody): Flow<ResultadoApi<JsonEncuesta>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { downloadEncuesta(body) }
        )

    override fun apiQueryLogin(body: RequestBody): Flow<ResultadoApi<Login>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { queryLogin(body) }
        )

    override fun apiQueryConsulta(body: RequestBody): Flow<ResultadoApi<JsonConsulta>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { queryConsulta(body) }
        )

    override fun apiQueryPedimap(body: RequestBody): Flow<ResultadoApi<JsonPedimap>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { queryPedimap(body) }
        )

    override fun apiQuerySupervisorBajas(body: RequestBody): Flow<ResultadoApi<JsonBajaSupervisor>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { querySupervisorBajas(body) }
        )

    override fun apiQueryVendedorBajas(body: RequestBody): Flow<ResultadoApi<JsonBajaVendedor>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { queryVendedorBajas(body) }
        )

    override fun apiReportPreventa(body: RequestBody): Flow<ResultadoApi<JsonVolumen>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportPreventa(body) }
        )

    override fun apiReportCobertura(body: RequestBody): Flow<ResultadoApi<JsonCoberturaCartera>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportCobertura(body) }
        )

    override fun apiReportCoberturaDetalle(body: RequestBody): Flow<ResultadoApi<JsonDetalleCobertura>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportCoberturaDetalle(body) }
        )

    override fun apiReportCartera(body: RequestBody): Flow<ResultadoApi<JsonCoberturaCartera>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportCartera(body) }
        )

    override fun apiReportGeneral(body: RequestBody): Flow<ResultadoApi<JsonPedido>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportGeneral(body) }
        )

    override fun apiReportClienteCambio(body: RequestBody): Flow<ResultadoApi<JsonCambio>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportClienteCambio(body) }
        )

    override fun apiReportEmpleadoCambio(body: RequestBody): Flow<ResultadoApi<JsonCambio>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportEmpleadoCambio(body) }
        )

    override fun apiReportSoles(body: RequestBody): Flow<ResultadoApi<JsonSoles>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportSoles(body) }
        )

    override fun apiReportSolesGenerico(body: RequestBody): Flow<ResultadoApi<JsonGenerico>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportSolesGenerico(body) }
        )

    override fun apiReportCoberturaPendiente(body: RequestBody): Flow<ResultadoApi<JsonCoberturados>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportCoberturaPendiente(body) }
        )

    override fun apiReportEmpleado(body: RequestBody): Flow<ResultadoApi<JsonPedidoGeneral>> =
        remoteFlowCall(
            setupHolder = { downloadSource },
            block = { reportEmpleado(body) }
        )

    override fun apiSendRegistro(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendRegistro(body) }
        )

    override fun apiSendSeguimiento(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendSeguimiento(body) }
        )

    override fun apiSendVisita(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendVisita(body) }
        )

    override fun apiSendAlta(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendAlta(body) }
        )

    override fun apiSendAltaDetalle(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendAltaDetalle(body) }
        )

    override fun apiSendAltaFoto(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendAltaFoto(body) }
        )

    override fun apiSendBaja(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendBaja(body) }
        )

    override fun apiSendConfirmarBaja(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendConfirmarBaja(body) }
        )

    override fun apiSendRespuesta(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendRespuesta(body) }
        )

    override fun apiSendFoto(body: RequestBody): Flow<ResultadoApi<JsonResponseAny>> =
        remoteFlowCall(
            setupHolder = { uploadSource },
            block = { sendFoto(body) }
        )
}