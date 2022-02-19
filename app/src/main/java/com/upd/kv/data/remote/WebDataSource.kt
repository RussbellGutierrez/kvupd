package com.upd.kv.data.remote

import com.upd.kv.data.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
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

    suspend fun getWebEncuesta(body: RequestBody): Response<JEncuesta> {
        return web.getApiEncuesta(body)
    }
}