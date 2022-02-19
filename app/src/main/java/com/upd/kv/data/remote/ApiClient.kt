package com.upd.kv.data.remote

import com.upd.kv.data.model.*
import com.upd.kv.utils.Constant.API_CLIENTE
import com.upd.kv.utils.Constant.API_CONFIGURACION
import com.upd.kv.utils.Constant.API_DISTRITO
import com.upd.kv.utils.Constant.API_EMPLEADO
import com.upd.kv.utils.Constant.API_ENCUESTA
import com.upd.kv.utils.Constant.API_LOGIN
import com.upd.kv.utils.Constant.API_NEGOCIO
import com.upd.kv.utils.Constant.API_REGISTRO
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
}