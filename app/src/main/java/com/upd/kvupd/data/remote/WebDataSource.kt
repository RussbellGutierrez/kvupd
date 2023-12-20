package com.upd.kvupd.data.remote

import com.upd.kvupd.data.model.*
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class WebDataSource @Inject constructor(private val web: ApiClient) {

    suspend fun loginUser(body: RequestBody): Response<Login> {
        return web.loginUser(body)
    }

    suspend fun getWebConfiguracion(body: RequestBody): Response<JConfig> {
        return web.getApiConfig(body)
    }

    suspend fun registerWebDevice(body: RequestBody): Response<JObj> {
        return web.registerDevice(body)
    }

    suspend fun getWebClientes(body: RequestBody): Response<JCliente> {
        return web.getApiClientes(body)
    }

    suspend fun getWebEmpleados(body: RequestBody): Response<JVendedores> {
        return web.getApiEmpleados(body)
    }

    suspend fun getWebDistritos(body: RequestBody): Response<JCombo> {
        return web.getApiDistritos(body)
    }

    suspend fun getWebNegocios(body: RequestBody): Response<JCombo> {
        return web.getApiNegocios(body)
    }

    suspend fun getWebRutas(body: RequestBody): Response<JRuta> {
        return web.getApiRutas(body)
    }

    suspend fun getWebEncuesta(body: RequestBody): Response<JEncuesta> {
        return web.getApiEncuesta(body)
    }

    suspend fun getWebConsulta(body: RequestBody): Response<JConsulta> {
        return web.getApiConsulta(body)
    }

    suspend fun getWebPreventa(body: RequestBody): Response<JVolumen> {
        return web.getApiPreventa(body)
    }

    suspend fun getWebCobertura(body: RequestBody): Response<JCobCart> {
        return web.getApiCobertura(body)
    }

    suspend fun getWebCartera(body: RequestBody): Response<JCobCart> {
        return web.getApiCartera(body)
    }

    suspend fun getWebPedidos(body: RequestBody): Response<JPedido> {
        return web.getApiPedidos(body)
    }

    suspend fun getWebCambiosCli(body: RequestBody): Response<JCambio> {
        return web.getApiClienteCambios(body)
    }

    suspend fun getWebCambiosEmp(body: RequestBody): Response<JCambio> {
        return web.getApiEmpleadoCambios(body)
    }

    suspend fun getWebVisicooler(body: RequestBody): Response<JVisicooler> {
        return web.getApiVisicooler(body)
    }

    suspend fun getWebVisisuper(body: RequestBody): Response<JVisisuper> {
        return web.getApiVisisuper(body)
    }

    suspend fun getWebUmes(body: RequestBody): Response<JUmes> {
        return web.getApiUmes(body)
    }

    suspend fun getWebSoles(body: RequestBody): Response<JSoles> {
        return web.getApiSoles(body)
    }

    suspend fun getWebUmesGenerico(body: RequestBody): Response<JGenerico> {
        return  web.getApiUmesGenerico(body)
    }

    suspend fun getWebSolesGenerico(body: RequestBody): Response<JGenerico> {
        return web.getApiSolesGenerico(body)
    }

    suspend fun getWebUmesDetalle(body: RequestBody): Response<JGenerico> {
        return web.getApiUmesDetalle(body)
    }

    suspend fun getWebCoberturaDetalle(body: RequestBody): Response<JDetCob> {
        return web.getApiCoberturaDetalle(body)
    }

    suspend fun getWebSolesDetalle(body: RequestBody): Response<JGenerico> {
        return web.getApiSolesDetalle(body)
    }

    suspend fun getWebCoberturaPendiente(body: RequestBody): Response<JCoberturados> {
        return web.getApiCoberturaPendiente(body)
    }

    suspend fun getWebPedidosRealizados(body: RequestBody): Response<JPediGen> {
        return web.getApiPedidosRealizados(body)
    }

    suspend fun getWebPedimap(body: RequestBody): Response<JPedimap> {
        return web.getApiPedimap(body)
    }

    suspend fun getWebBajaVendedor(body: RequestBody): Response<JBajaVendedor> {
        return web.getApiBajaVendedor(body)
    }

    suspend fun getWebBajaSupervisor(body: RequestBody): Response<JBajaSupervisor> {
        return web.getApiBajaSupervisor(body)
    }

    suspend fun setServerSeguimiento(body: RequestBody): Response<JObj> {
        return  web.setApiSeguimiento(body)
    }

    suspend fun setServerVisita(body: RequestBody): Response<JObj> {
        return web.setApiVisita(body)
    }

    suspend fun setServerAlta(body: RequestBody): Response<JObj> {
        return web.setApiAlta(body)
    }

    suspend fun setServerAltaDatos(body: RequestBody): Response<JObj> {
        return web.setApiAltadatos(body)
    }

    suspend fun setServerBaja(body: RequestBody): Response<JObj> {
        return web.setApiBaja(body)
    }

    suspend fun setServerBajaEstados(body: RequestBody): Response<JObj> {
        return web.setApiBajaestado(body)
    }

    suspend fun setServerRespuestas(body: RequestBody): Response<JObj> {
        return web.setApiRespuesta(body)
    }

    suspend fun setServerFotos(body: RequestBody): Response<JFoto> {
        return web.setApiFoto(body)
    }

    suspend fun setServerAltaFotos(body: RequestBody): Response<JFoto> {
        return web.setApiAltaFoto(body)
    }
}