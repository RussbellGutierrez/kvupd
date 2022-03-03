package com.upd.kventas.data.remote

import com.upd.kventas.data.model.*
import com.upd.kventas.utils.Constant.API_CARTERA
import com.upd.kventas.utils.Constant.API_CLICAMBIO
import com.upd.kventas.utils.Constant.API_CLIENTE
import com.upd.kventas.utils.Constant.API_COBERTURA
import com.upd.kventas.utils.Constant.API_COBPEN
import com.upd.kventas.utils.Constant.API_CONFIGURACION
import com.upd.kventas.utils.Constant.API_DISTRITO
import com.upd.kventas.utils.Constant.API_EMPCAMBIO
import com.upd.kventas.utils.Constant.API_EMPLEADO
import com.upd.kventas.utils.Constant.API_EMPMARCADOR
import com.upd.kventas.utils.Constant.API_ENCUESTA
import com.upd.kventas.utils.Constant.API_LOGIN
import com.upd.kventas.utils.Constant.API_NEGOCIO
import com.upd.kventas.utils.Constant.API_PREVENTA
import com.upd.kventas.utils.Constant.API_REGISTRO
import com.upd.kventas.utils.Constant.API_REPOEMP
import com.upd.kventas.utils.Constant.API_REPOGEN
import com.upd.kventas.utils.Constant.API_SOLES
import com.upd.kventas.utils.Constant.API_SOLESGEN
import com.upd.kventas.utils.Constant.API_UMES
import com.upd.kventas.utils.Constant.API_UMESDET
import com.upd.kventas.utils.Constant.API_UMESGEN
import com.upd.kventas.utils.Constant.API_VISIC
import com.upd.kventas.utils.Constant.API_VISICSUPER
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiClient {

    /**PARA OBTENER LA RESPUESTA SIN PARSEAR A TRAVES DE RETROFIT, SE DEBE USAR LA CLASE "ResponseBody"
     * LA CUAL NOS DEVOLVERA UN "response.data?.string()" CON LOS DATOS DEL API**/

    @POST(API_LOGIN)
    suspend fun loginUser(@Body body: RequestBody): Response<Login>//@param usuario,clave,empresa

    @POST(API_CONFIGURACION)
    suspend fun getApiConfig(@Body body:RequestBody): Response<JConfig>//@param imei

    @POST(API_REGISTRO)
    suspend fun registerDevice(@Body body:RequestBody): Response<JObj>//@param imei,modelo,version,empresa

    @POST(API_CLIENTE)
    suspend fun getApiClientes(@Body body: RequestBody): Response<JCliente>//@param empleado,fecha,empresa

    @POST(API_EMPLEADO)
    suspend fun getApiEmpleados(@Body body: RequestBody): Response<JVendedores>//@param empleado,empresa

    @POST(API_DISTRITO)
    suspend fun getApiDistritos(@Body body: RequestBody): Response<JCombo>//@param empresa

    @POST(API_NEGOCIO)
    suspend fun getApiNegocios(@Body body: RequestBody): Response<JCombo>//@param empresa

    @POST(API_ENCUESTA)
    suspend fun getApiEncuesta(@Body body: RequestBody): Response<JEncuesta>//@param empleado,empresa

    @POST(API_PREVENTA)
    suspend fun getApiPreventa(@Body body: RequestBody): Response<JVolumen>//@param empleado,empresa

    @POST(API_COBERTURA)
    suspend fun getApiCobertura(@Body body: RequestBody): Response<JCobCart>//@param empleado,empresa

    @POST(API_CARTERA)
    suspend fun getApiCartera(@Body body: RequestBody): Response<JCobCart>//@param empleado,empresa

    @POST(API_REPOGEN)
    suspend fun getApiPedidos(@Body body: RequestBody): Response<JPedido>//@param empleado,empresa

    @POST(API_CLICAMBIO)
    suspend fun getApiClienteCambios(@Body body: RequestBody): Response<JCambio>//@param empleado,empresa

    @POST(API_EMPCAMBIO)
    suspend fun getApiEmpleadoCambios(@Body body: RequestBody): Response<JCambio>//@param empleado,empresa

    @POST(API_VISIC)
    suspend fun getApiVisicooler(@Body body: RequestBody): Response<JVisicooler>//@param empleado,empresa

    @POST(API_VISICSUPER)
    suspend fun getApiVisisuper(@Body body: RequestBody): Response<JVisisuper>//@param empleado,empresa

    @POST(API_UMES)
    suspend fun getApiUmes(@Body body: RequestBody): Response<JUmes>//@param empleado,empresa

    @POST(API_SOLES)
    suspend fun getApiSoles(@Body body: RequestBody): Response<JSoles>//@param empleado,empresa

    @POST(API_UMESGEN)
    suspend fun getApiUmesGenerico(@Body body: RequestBody): Response<JGenerico>//@param empleado,empresa,linea

    @POST(API_SOLESGEN)
    suspend fun getApiSolesGenerico(@Body body: RequestBody): Response<JGenerico>//@param empleado,empresa,linea

    @POST(API_UMESDET)
    suspend fun getApiUmesDetalle(@Body body: RequestBody): Response<JGenerico>//@param empleado,empresa,generico

    @POST(API_PREVENTA)
    suspend fun getApiSolesDetalle(@Body body: RequestBody): Response<JGenerico>//@param empleado,empresa,linea

    @POST(API_COBPEN)
    suspend fun getApiCoberturaPendiente(@Body body: RequestBody): Response<JCoberturados>//@param empleado, empresa

    @POST(API_REPOEMP)
    suspend fun getApiPedidosRealizados(@Body body: RequestBody): Response<JPediGen>//@param empleado, empresa

    @POST(API_EMPMARCADOR)
    suspend fun getApiPedimap(@Body body: RequestBody): Response<JPedimap>//@param empleado, empresa
}