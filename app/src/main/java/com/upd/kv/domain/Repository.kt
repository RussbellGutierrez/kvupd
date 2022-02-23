package com.upd.kv.domain

import com.upd.kv.data.model.*
import com.upd.kv.utils.Network
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface Repository {
    //  Room Functions
    fun getFlowConfig(): Flow<List<Config>>
    fun getFlowRowCliente(): Flow<List<RowCliente>>
    fun getFlowLocation(): Flow<List<TSeguimiento>>
    fun getFlowMarker(): Flow<List<MarkerMap>>
    suspend fun getConfig(): List<Config>
    suspend fun getClientes(): List<Cliente>
    suspend fun getEmpleados(): List<Vendedor>
    suspend fun getDistritos(): List<Combo>
    suspend fun getNegocios(): List<Combo>
    suspend fun getClienteDetail(cliente: String): List<DataCliente>
    suspend fun isClienteBaja(cliente: String): Boolean
    suspend fun getStarterTime(): Long?
    suspend fun workDay(): Boolean?

    suspend fun saveConfiguracion(config: List<Config>)
    suspend fun saveClientes(cliente: List<Cliente>)
    suspend fun saveEmpleados(empleado: List<Vendedor>)
    suspend fun saveDistrito(distrito: List<Combo>)
    suspend fun saveNegocio(negocio: List<Combo>)
    suspend fun saveEncuesta(encuesta: List<Encuesta>)
    suspend fun saveSeguimiento(seguimiento: TSeguimiento)
    suspend fun saveVisita(visita: TVisita)
    suspend fun saveEstado(estado: TEstado)
    suspend fun saveBaja(baja: TBaja)

    suspend fun deleteClientes()
    suspend fun deleteEmpleados()
    suspend fun deleteDistritos()
    suspend fun deleteNegocios()
    suspend fun deleteEncuesta()
    suspend fun deleteSeguimiento()
    suspend fun deleteVisita()
    suspend fun deleteEstado()
    suspend fun deleteBaja()

    //  Retrofit Functions
    suspend fun loginAdministrator(body: RequestBody): Flow<Network<Login>>
    suspend fun registerWebDevice(body: RequestBody): Flow<Network<JObj>>
    suspend fun getWebConfiguracion(body: RequestBody): Flow<Network<JConfig>>
    suspend fun getWebClientes(body: RequestBody): Flow<Network<JCliente>>
    suspend fun getWebEmpleados(body: RequestBody): Flow<Network<JVendedores>>
    suspend fun getWebDistritos(body: RequestBody): Flow<Network<JCombo>>
    suspend fun getWebNegocios(body: RequestBody): Flow<Network<JCombo>>
    suspend fun getWebEncuesta(body: RequestBody): Flow<Network<JEncuesta>>
}