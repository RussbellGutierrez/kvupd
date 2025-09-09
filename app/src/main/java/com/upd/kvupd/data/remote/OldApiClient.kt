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
import com.upd.kvupd.data.model.JsonPedidoGeneral
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.JsonRuta
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVendedor
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.data.model.Login
import com.upd.kvupd.utils.OldConstant.API_BAJAESTLIS
import com.upd.kvupd.utils.OldConstant.API_BAJALIS
import com.upd.kvupd.utils.OldConstant.API_CARTERA
import com.upd.kvupd.utils.OldConstant.API_CLICAMBIO
import com.upd.kvupd.utils.OldConstant.API_CLIENTE
import com.upd.kvupd.utils.OldConstant.API_COBDET
import com.upd.kvupd.utils.OldConstant.API_COBERTURA
import com.upd.kvupd.utils.OldConstant.API_COBPEN
import com.upd.kvupd.utils.OldConstant.API_CONFIGURACION
import com.upd.kvupd.utils.OldConstant.API_CONSULTA
import com.upd.kvupd.utils.OldConstant.API_DISTRITO
import com.upd.kvupd.utils.OldConstant.API_EMPCAMBIO
import com.upd.kvupd.utils.OldConstant.API_EMPLEADO
import com.upd.kvupd.utils.OldConstant.API_EMPMARCADOR
import com.upd.kvupd.utils.OldConstant.API_ENCUESTA
import com.upd.kvupd.utils.OldConstant.API_LOGIN
import com.upd.kvupd.utils.OldConstant.API_NEGOCIO
import com.upd.kvupd.utils.OldConstant.API_PREVENTA
import com.upd.kvupd.utils.OldConstant.API_REPOEMP
import com.upd.kvupd.utils.OldConstant.API_REPOGEN
import com.upd.kvupd.utils.OldConstant.API_RUTA
import com.upd.kvupd.utils.OldConstant.API_SOLES
import com.upd.kvupd.utils.OldConstant.API_SOLESGEN
import com.upd.kvupd.utils.OldConstant.API_UMESDET
import com.upd.kvupd.utils.OldConstant.API_UMESGEN
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OldApiClient {

    /**PARA OBTENER LA RESPUESTA SIN PARSEAR A TRAVES DE RETROFIT, SE DEBE USAR LA CLASE "ResponseBody"
     * LA CUAL NOS DEVOLVERA UN "response.data?.string()" CON LOS DATOS DEL API**/

    @POST(API_LOGIN)
    suspend fun loginUser(@Body body: RequestBody): Response<Login>//@param usuario,clave,empresa

    @POST(API_CONFIGURACION)
    suspend fun getApiConfig(@Body body:RequestBody): Response<JsonConfiguracion>//@param imei

    //@POST(API_REGISTRO)
    //suspend fun registerDevice(@Body body:RequestBody): Response<JObj>//@param imei,modelo,version,empresa

    @POST(API_CLIENTE)
    suspend fun getApiClientes(@Body body: RequestBody): Response<JsonCliente>//@param empleado,fecha,empresa

    @POST(API_EMPLEADO)
    suspend fun getApiEmpleados(@Body body: RequestBody): Response<JsonVendedor>//@param empleado,empresa

    @POST(API_DISTRITO)
    suspend fun getApiDistritos(@Body body: RequestBody): Response<JsonDistrito>//@param empresa

    @POST(API_NEGOCIO)
    suspend fun getApiNegocios(@Body body: RequestBody): Response<JsonNegocio>//@param empresa

    @POST(API_RUTA)
    suspend fun getApiRutas(@Body body: RequestBody): Response<JsonRuta>//@param empleado, empresa

    @POST(API_ENCUESTA)
    suspend fun getApiEncuesta(@Body body: RequestBody): Response<JsonEncuesta>//@param empleado,empresa

    @POST(API_CONSULTA)
    suspend fun getApiConsulta(@Body body: RequestBody): Response<JsonConsulta>//@param fecha,empresa

    @POST(API_PREVENTA)
    suspend fun getApiPreventa(@Body body: RequestBody): Response<JsonVolumen>//@param empleado,empresa

    @POST(API_COBERTURA)
    suspend fun getApiCobertura(@Body body: RequestBody): Response<JsonCoberturaCartera>//@param empleado,empresa

    @POST(API_CARTERA)
    suspend fun getApiCartera(@Body body: RequestBody): Response<JsonCoberturaCartera>//@param empleado,empresa

    @POST(API_REPOGEN)
    suspend fun getApiPedidos(@Body body: RequestBody): Response<JsonPedido>//@param empleado,empresa

    @POST(API_CLICAMBIO)
    suspend fun getApiClienteCambios(@Body body: RequestBody): Response<JsonCambio>//@param empleado,empresa

    @POST(API_EMPCAMBIO)
    suspend fun getApiEmpleadoCambios(@Body body: RequestBody): Response<JsonCambio>//@param empleado,empresa

    //@POST(API_VISIC)
    //suspend fun getApiVisicooler(@Body body: RequestBody): Response<JVisicooler>//@param empleado,empresa

    //@POST(API_VISICSUPER)
    //suspend fun getApiVisisuper(@Body body: RequestBody): Response<JVisisuper>//@param empleado,empresa

    //@POST(API_UMES)
    //suspend fun getApiUmes(@Body body: RequestBody): Response<JUmes>//@param empleado,empresa

    @POST(API_SOLES)
    suspend fun getApiSoles(@Body body: RequestBody): Response<JsonSoles>//@param empleado,empresa

    @POST(API_UMESGEN)
    suspend fun getApiUmesGenerico(@Body body: RequestBody): Response<JsonGenerico>//@param empleado,empresa,linea

    @POST(API_SOLESGEN)
    suspend fun getApiSolesGenerico(@Body body: RequestBody): Response<JsonGenerico>//@param empleado,empresa,linea

    @POST(API_UMESDET)
    suspend fun getApiUmesDetalle(@Body body: RequestBody): Response<JsonGenerico>//@param empleado,empresa,generico

    @POST(API_COBDET)
    suspend fun getApiCoberturaDetalle(@Body body: RequestBody): Response<JsonDetalleCobertura>//@param empleado, empresa

    @POST(API_PREVENTA)
    suspend fun getApiSolesDetalle(@Body body: RequestBody): Response<JsonGenerico>//@param empleado,empresa,linea

    @POST(API_COBPEN)
    suspend fun getApiCoberturaPendiente(@Body body: RequestBody): Response<JsonCoberturados>//@param empleado, empresa

    @POST(API_REPOEMP)
    suspend fun getApiPedidosRealizados(@Body body: RequestBody): Response<JsonPedidoGeneral>//@param empleado, empresa

    @POST(API_EMPMARCADOR)
    suspend fun getApiPedimap(@Body body: RequestBody): Response<JsonPedimap>//@param empleado, empresa

    @POST(API_BAJAESTLIS)
    suspend fun getApiBajaVendedor(@Body body: RequestBody): Response<JsonBajaVendedor>//@param empleado, empresa

    @POST(API_BAJALIS)
    suspend fun getApiBajaSupervisor(@Body body: RequestBody): Response<JsonBajaSupervisor>//@param empleado, empresa

    /*  Send Server */
    //@POST(API_SEGUIMIENTO)
    //suspend fun setApiSeguimiento(@Body body: RequestBody): Response<JObj>//@param empresa, fecha, empleado, longitud, latitud, precision, imei, bateria, sucursal, esquema

    //@POST(API_VISITA)
    //suspend fun setApiVisita(@Body body: RequestBody): Response<JObj>//@param empresa, cliente, fecha, empleado, longitud, latitud, motivo, precision, sucursal, esquema

    //@POST(API_ALTA)
    //suspend fun setApiAlta(@Body body: RequestBody): Response<JObj>//@param empresa, empleado, fecha, id, longitud, latitud, precision, sucursal, esquema

    //@POST(API_ALTADETALLE)
    //suspend fun setApiAltadatos(@Body body: RequestBody): Response<JObj>//@param empresa, empleado, id, appaterno, apmaterno, nombre, razon, tipo, tipodoc, giro, movil1, movil2, email, calle, urbanizacion, altura, distrito, ruta, secuencia, sucursal, esquema

    //@POST(API_BAJA)
    //suspend fun setApiBaja(@Body body: RequestBody): Response<JObj>//@param empresa, empleado, fecha, cliente, motivo, observacion, xcoord, ycoord, precision, anulado

    //@POST(API_BAJACONFIR)
    //suspend fun setApiBajaestado(@Body body: RequestBody): Response<JObj>//@param empresa, empleado, fecha, cliente, cfecha, observacion, precision, xcoord, ycoord, confirmar

    //@POST(API_RESPUESTA)
    //suspend fun setApiRespuesta(@Body body: RequestBody): Response<JObj>//@param empresa, empleado, cliente, encuesta, pregunta, respuesta, fecha

    //@POST(API_FOTO)
    //suspend fun setApiFoto(@Body body: RequestBody): Response<JRespuestaFoto>//@param empresa, empleado, cliente, encuesta, sucursal, foto

    //@POST(API_ALTAFOTO)
    //suspend fun setApiAltaFoto(@Body body: RequestBody): Response<JRespuestaFoto>//@param fecha, id, empleado, empresa, foto
}