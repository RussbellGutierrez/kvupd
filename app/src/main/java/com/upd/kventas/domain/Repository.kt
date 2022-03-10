package com.upd.kventas.domain

import android.location.Location
import com.upd.kventas.data.model.*
import com.upd.kventas.utils.Network
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface Repository {
    //  Room Functions
    fun getFlowConfig(): Flow<List<Config>>
    fun getFlowRowCliente(): Flow<List<RowCliente>>
    fun getFlowLocation(): Flow<List<TSeguimiento>>
    fun getFlowMarker(): Flow<List<MarkerMap>>
    fun getFlowAltas(): Flow<List<TAlta>>
    fun getFlowDistritos(): Flow<List<Combo>>
    fun getFlowNegocios(): Flow<List<Combo>>
    fun getFlowBajas(): Flow<List<TBaja>>
    fun getFlowRowBaja(): Flow<List<RowBaja>>
    fun getFlowRutas(): Flow<List<TRutas>>

    suspend fun getConfig(): List<Config>
    suspend fun getClientes(): List<Cliente>
    suspend fun getEmpleados(): List<Vendedor>
    suspend fun getDistritos(): List<Combo>
    suspend fun getNegocios(): List<Combo>
    suspend fun getRutas(): List<Ruta>
    suspend fun getEncuestas(): List<Encuesta>
    suspend fun getClienteDetail(cliente: String): List<DataCliente>
    suspend fun getDataAlta(alta: String): DataCliente
    suspend fun getAltaDatoSpecific(alta: String): TADatos?
    suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper
    suspend fun isClienteBaja(cliente: String): Boolean
    suspend fun getLastAlta(): TAlta?
    suspend fun processAlta(fecha: String, location: Location)
    suspend fun getStarterTime(): Long?
    suspend fun getFinishTime(): Long?
    suspend fun workDay(): Boolean?

    suspend fun saveConfiguracion(config: List<Config>)
    suspend fun saveClientes(cliente: List<Cliente>)
    suspend fun saveEmpleados(empleado: List<Vendedor>)
    suspend fun saveDistritos(distrito: List<Combo>)
    suspend fun saveRutas(ruta: List<Ruta>)
    suspend fun saveNegocios(negocio: List<Combo>)
    suspend fun saveEncuesta(encuesta: List<Encuesta>)
    suspend fun saveSeguimiento(seguimiento: TSeguimiento)
    suspend fun saveVisita(visita: TVisita)
    suspend fun saveEstado(estado: TEstado)
    suspend fun saveBaja(baja: TBaja)
    suspend fun saveAlta(alta: TAlta)
    suspend fun saveAltaDatos(da: TADatos)
    suspend fun saveBajaSuper(baja: List<BajaSupervisor>)
    suspend fun saveBajaEstado(estado: TBEstado)

    suspend fun getServerSeguimiento(estado: String): List<TSeguimiento>
    suspend fun getServerVisita(estado: String): List<TVisita>
    suspend fun getServerAlta(estado: String): List<TAlta>
    suspend fun getServerAltadatos(estado: String): List<TADatos>
    suspend fun getServerBaja(estado: String): List<TBaja>
    suspend fun getServerBajaestado(estado: String): List<TBEstado>

    suspend fun updateLocationAlta(locationAlta: LocationAlta)
    suspend fun updateMiniAlta(miniUpdAlta: MiniUpdAlta)
    suspend fun updateAltaDatos(upd: TADatos)
    suspend fun updateMiniBaja(miniUpdBaja: MiniUpdBaja)

    suspend fun deleteClientes()
    suspend fun deleteEmpleados()
    suspend fun deleteDistritos()
    suspend fun deleteNegocios()
    suspend fun deleteRutas()
    suspend fun deleteEncuesta()
    suspend fun deleteSeguimiento()
    suspend fun deleteVisita()
    suspend fun deleteEstado()
    suspend fun deleteBaja()
    suspend fun deleteAlta()
    suspend fun deleteAltaDatos()
    suspend fun deleteBajaSuper()
    suspend fun deleteBajaEstado()

    //  Retrofit Functions
    suspend fun loginAdministrator(body: RequestBody): Flow<Network<Login>>
    suspend fun registerWebDevice(body: RequestBody): Flow<Network<JObj>>
    suspend fun getWebConfiguracion(body: RequestBody): Flow<Network<JConfig>>
    suspend fun getWebClientes(body: RequestBody): Flow<Network<JCliente>>
    suspend fun getWebEmpleados(body: RequestBody): Flow<Network<JVendedores>>
    suspend fun getWebDistritos(body: RequestBody): Flow<Network<JCombo>>
    suspend fun getWebNegocios(body: RequestBody): Flow<Network<JCombo>>
    suspend fun getWebRutas(body: RequestBody): Flow<Network<JRuta>>
    suspend fun getWebEncuesta(body: RequestBody): Flow<Network<JEncuesta>>

    suspend fun getWebPreventa(body: RequestBody): Flow<Network<JVolumen>>
    suspend fun getWebCobertura(body: RequestBody): Flow<Network<JCobCart>>
    suspend fun getWebCartera(body: RequestBody): Flow<Network<JCobCart>>
    suspend fun getWebPedidos(body: RequestBody): Flow<Network<JPedido>>
    suspend fun getWebCambiosCli(body: RequestBody): Flow<Network<JCambio>>
    suspend fun getWebCambiosEmp(body: RequestBody): Flow<Network<JCambio>>
    suspend fun getWebVisicooler(body: RequestBody): Flow<Network<JVisicooler>>
    suspend fun getWebVisisuper(body: RequestBody): Flow<Network<JVisisuper>>
    suspend fun getWebUmes(body: RequestBody): Flow<Network<JUmes>>
    suspend fun getWebSoles(body: RequestBody): Flow<Network<JSoles>>

    suspend fun getWebUmesGenerico(body: RequestBody): Flow<Network<JGenerico>>
    suspend fun getWebSolesGenerico(body: RequestBody): Flow<Network<JGenerico>>
    suspend fun getWebUmesDetalle(body: RequestBody): Flow<Network<JGenerico>>
    suspend fun getWebSolesDetalle(body: RequestBody): Flow<Network<JGenerico>>
    suspend fun getWebCoberturaPendiente(body: RequestBody): Flow<Network<JCoberturados>>
    suspend fun getWebPedidosRealizados(body: RequestBody): Flow<Network<JPediGen>>

    suspend fun getWebPedimap(body: RequestBody): Flow<Network<JPedimap>>
    suspend fun getWebBajaVendedor(body: RequestBody): Flow<Network<JBajaVendedor>>
    suspend fun getWebBajaSupervisor(body: RequestBody): Flow<Network<JBajaSupervisor>>

    //  Send Server
    suspend fun setWebSeguimiento(body: RequestBody): Flow<Network<JObj>>
    suspend fun setWebVisita(body: RequestBody): Flow<Network<JObj>>
    suspend fun setWebAlta(body: RequestBody): Flow<Network<JObj>>
    suspend fun setWebAltaDatos(body: RequestBody): Flow<Network<JObj>>
    suspend fun setWebBaja(body: RequestBody): Flow<Network<JObj>>
    suspend fun setWebBajaEstados(body: RequestBody): Flow<Network<JObj>>
}