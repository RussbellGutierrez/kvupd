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
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.data.model.JsonRuta
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVendedor
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.utils.ApisConsultaDatos.FETCH_CONSULTA
import com.upd.kvupd.utils.ApisConsultaDatos.FETCH_ESTADOLISTA_BAJAS
import com.upd.kvupd.utils.ApisConsultaDatos.FETCH_LISTA_BAJAS
import com.upd.kvupd.utils.ApisConsultaDatos.FETCH_PEDIMAP
import com.upd.kvupd.utils.ApisDescargaInicial.API_CLIENTE
import com.upd.kvupd.utils.ApisDescargaInicial.API_CONFIGURACION
import com.upd.kvupd.utils.ApisDescargaInicial.API_DISTRITO
import com.upd.kvupd.utils.ApisDescargaInicial.API_EMPLEADO
import com.upd.kvupd.utils.ApisDescargaInicial.API_ENCUESTA
import com.upd.kvupd.utils.ApisDescargaInicial.API_NEGOCIO
import com.upd.kvupd.utils.ApisDescargaInicial.API_RUTA
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_ALTA
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_ALTADETALLE
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_ALTAFOTO
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_BAJA
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_CONFIRMAR_BAJA
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_FOTOS
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_REGISTRO
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_RESPUESTA
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_SEGUIMIENTO
import com.upd.kvupd.utils.ApisEnviarServidor.SEND_VISITA
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_CARTERA
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_CLIENTE_CAMBIO
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_COBERTURA
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_COBERTURA_DETALLE
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_COBERTURA_PENDIENTE
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_EMPLEADO
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_EMPLEADO_CAMBIO
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_GENERAL
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_PREVENTA
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_SOLES
import com.upd.kvupd.utils.ApisReporteVentas.REPORT_SOLES_GENERICO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // DESCARGA INICIAL DE DATOS
    @POST(API_CONFIGURACION) // Parametros: imei (android_uid)
    suspend fun downloadConfiguracion(@Body body: RequestBody): Response<JsonConfiguracion>
    @POST(API_CLIENTE) // Parametros: empleado,fecha,empresa
    suspend fun downloadCliente(@Body body: RequestBody): Response<JsonCliente>
    @POST(API_EMPLEADO) // Parametros: empleado,empresa
    suspend fun downloadEmpleado(@Body body: RequestBody): Response<JsonVendedor>
    @POST(API_RUTA) // Parametros: empleado, empresa
    suspend fun downloadRuta(@Body body: RequestBody): Response<JsonRuta>
    @POST(API_DISTRITO) // Parametros: empresa
    suspend fun downloadDistrito(@Body body: RequestBody): Response<JsonDistrito>
    @POST(API_NEGOCIO) // Parametros: empresa
    suspend fun downloadNegocio(@Body body: RequestBody): Response<JsonNegocio>
    @POST(API_ENCUESTA) // Parametros: empleado,empresa
    suspend fun downloadEncuesta(@Body body: RequestBody): Response<JsonEncuesta>


    // CONSULTA DE DATOS EN SERVIDOR
    @POST(FETCH_CONSULTA) // Parametros: fecha,empresa
    suspend fun queryConsulta(@Body body: RequestBody): Response<JsonConsulta>
    @POST(FETCH_PEDIMAP) // Parametros: empleado, empresa
    suspend fun queryPedimap(@Body body: RequestBody): Response<JsonPedimap>
    @POST(FETCH_LISTA_BAJAS) // Parametros: empleado, empresa
    suspend fun querySupervisorBajas(@Body body: RequestBody): Response<JsonBajaSupervisor>
    @POST(FETCH_ESTADOLISTA_BAJAS) // Parametros: empleado, empresa
    suspend fun queryVendedorBajas(@Body body: RequestBody): Response<JsonBajaVendedor>


    // ENVIAR DATOS AL SERVIDOR
    @POST(SEND_REGISTRO) // Parametros: imei (android_uid),modelo,version,empresa
    suspend fun sendRegistro(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_SEGUIMIENTO) // Parametros: empresa, fecha, empleado, longitud, latitud, precision, imei, bateria, sucursal, esquema
    suspend fun sendSeguimiento(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_VISITA) // Parametros: empresa, cliente, fecha, empleado, longitud, latitud, motivo, precision, sucursal, esquema
    suspend fun sendVisita(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_ALTA) // Parametros: empresa, empleado, fecha, id, longitud, latitud, precision, sucursal, esquema
    suspend fun sendAlta(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_ALTADETALLE) // Parametros: empresa, empleado, id, appaterno, apmaterno, nombre, razon, tipo, tipodoc, giro, movil1, movil2, email, calle, urbanizacion, altura, distrito, ruta, secuencia, sucursal, esquema
    suspend fun sendAltaDetalle(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_ALTAFOTO) // Parametros: fecha, id, empleado, empresa, foto
    suspend fun sendAltaFoto(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_BAJA) // Parametros: empresa, empleado, fecha, cliente, motivo, observacion, xcoord, ycoord, precision, anulado
    suspend fun sendBaja(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_CONFIRMAR_BAJA) // Parametros: empresa, empleado, fecha, cliente, cfecha, observacion, precision, xcoord, ycoord, confirmar
    suspend fun sendConfirmarBaja(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_RESPUESTA) // Parametros: empresa, empleado, cliente, encuesta, pregunta, respuesta, fecha
    suspend fun sendRespuesta(@Body body: RequestBody): Response<JsonResponseAny>
    @POST(SEND_FOTOS) // Parametros: empresa, empleado, cliente, encuesta, sucursal, foto
    suspend fun sendFoto(@Body body: RequestBody): Response<JsonResponseAny>


    // SOLICITAR DATOS DE REPORTE
    @POST(REPORT_PREVENTA) // Parametros: empleado,empresa
    suspend fun reportPreventa(@Body body: RequestBody): Response<JsonVolumen>
    @POST(REPORT_COBERTURA) // Parametros: empleado,empresa
    suspend fun reportCobertura(@Body body: RequestBody): Response<JsonCoberturaCartera>
    @POST(REPORT_COBERTURA_DETALLE) // Parametros: empleado,empresa
    suspend fun reportCoberturaDetalle(@Body body: RequestBody): Response<JsonDetalleCobertura>
    @POST(REPORT_CARTERA) // Parametros: empleado,empresa
    suspend fun reportCartera(@Body body: RequestBody): Response<JsonCoberturaCartera>
    @POST(REPORT_GENERAL) // Parametros: empleado,empresa
    suspend fun reportGeneral(@Body body: RequestBody): Response<JsonPedido>
    @POST(REPORT_CLIENTE_CAMBIO) // Parametros: empleado,empresa
    suspend fun reportClienteCambio(@Body body: RequestBody): Response<JsonCambio>
    @POST(REPORT_EMPLEADO_CAMBIO) // Parametros: empleado,empresa
    suspend fun reportEmpleadoCambio(@Body body: RequestBody): Response<JsonCambio>
    @POST(REPORT_SOLES) // Parametros: empleado,empresa
    suspend fun reportSoles(@Body body: RequestBody): Response<JsonSoles>
    @POST(REPORT_SOLES_GENERICO) // Parametros: empleado,empresa,linea
    suspend fun reportSolesGenerico(@Body body: RequestBody): Response<JsonGenerico>
    @POST(REPORT_COBERTURA_PENDIENTE) // Parametros: empleado,empresa
    suspend fun reportCoberturaPendiente(@Body body: RequestBody): Response<JsonCoberturados>
    @POST(REPORT_EMPLEADO) // Parametros: empleado,empresa
    suspend fun reportEmpleado(@Body body: RequestBody): Response<JsonPedidoGeneral>
}