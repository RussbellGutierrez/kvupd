package com.upd.kvupd.domain

import android.location.Location
import com.upd.kvupd.data.model.*
import com.upd.kvupd.utils.NetworkRetrofit
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface Repository {
    //  Room Functions
    fun getFlowConfig(): Flow<List<TConfiguracion>>
    fun getFlowSession(): Flow<TSesion>
    fun getFlowRowCliente(): Flow<List<RowCliente>>
    fun getFlowLocation(): Flow<List<TSeguimiento>>
    fun getFlowMarker(): Flow<List<MarkerMap>>
    fun getFlowAltas(): Flow<List<TAlta>>
    fun getFlowDistritos(): Flow<List<Combo>>
    fun getFlowNegocios(): Flow<List<Combo>>
    fun getFlowBajas(): Flow<List<TBaja>>
    fun getFlowRowBaja(): Flow<List<RowBaja>>
    fun getFlowRutas(): Flow<List<TRutas>>
    fun getFlowIncidencias(): Flow<List<TIncidencia>>

    suspend fun getSesion(): TSesion?
    suspend fun getConfig(): TConfiguracion?
    suspend fun getClientes(): List<Cliente>
    suspend fun getEmpleados(): List<Vendedor>
    suspend fun getDistritos(): List<Combo>
    suspend fun getNegocios(): List<Combo>
    suspend fun getRutas(): List<Ruta>
    suspend fun getListEncuestas(): List<Cabecera>
    suspend fun getPreguntas(): List<TEncuesta>
    suspend fun getClienteDetail(cliente: String): List<DataCliente>
    suspend fun getDataAlta(alta: String): DataCliente
    suspend fun getAltaDatoSpecific(alta: String): TADatos?
    suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper
    suspend fun isClienteBaja(cliente: String): Boolean
    suspend fun getLastAlta(): TAlta?
    suspend fun processAlta(fecha: String, location: Location)
    suspend fun isDataToday(today: String): Boolean
    suspend fun getStarterTime(): Long
    suspend fun getFinishTime(): Long
    suspend fun getIntoHours(): Boolean
    suspend fun getSeleccionado(): TEncuestaSeleccionado?
    suspend fun clienteRespondioActual(cliente: String): Boolean
    suspend fun clienteRespondioAntes(cliente: String): String

    suspend fun saveSesion(config: Config)
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
    suspend fun saveRespuestaOneByOne(respuesta: TRespuesta)
    suspend fun saveFoto(respuesta: TRespuesta)
    suspend fun saveSeleccionado(selec: TEncuestaSeleccionado)
    suspend fun saveRespuesta(respuesta: List<TRespuesta>)
    suspend fun saveIncidencia(respuesta: TIncidencia)

    suspend fun getServerSeguimiento(estado: String): List<TSeguimiento>
    suspend fun getServerVisita(estado: String): List<TVisita>
    suspend fun getServerAlta(estado: String): List<TAlta>
    suspend fun getServerAltadatos(estado: String): List<TADatos>
    suspend fun getServerBaja(estado: String): List<TBaja>
    suspend fun getServerBajaestado(estado: String): List<TBEstado>
    suspend fun getServerRespuesta(estado: String): List<TRespuesta>
    suspend fun getServerFoto(estado: String): List<TRespuesta>

    suspend fun updateLocationAlta(locationAlta: LocationAlta)
    suspend fun updateMiniAlta(miniUpdAlta: MiniUpdAlta)
    suspend fun updateAltaDatos(upd: TADatos)
    suspend fun updateMiniBaja(miniUpdBaja: MiniUpdBaja)

    suspend fun deleteConfig()
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
    suspend fun deleteSeleccionado()
    suspend fun deleteRespuesta()
    suspend fun deleteIncidencia()

    //  Retrofit Functions
    suspend fun loginAdministrator(body: RequestBody): Flow<NetworkRetrofit<Login>>
    suspend fun registerWebDevice(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun getWebConfiguracion(body: RequestBody): Flow<NetworkRetrofit<JConfig>>
    suspend fun getWebClientes(body: RequestBody): Flow<NetworkRetrofit<JCliente>>
    suspend fun getWebEmpleados(body: RequestBody): Flow<NetworkRetrofit<JVendedores>>
    suspend fun getWebDistritos(body: RequestBody): Flow<NetworkRetrofit<JCombo>>
    suspend fun getWebNegocios(body: RequestBody): Flow<NetworkRetrofit<JCombo>>
    suspend fun getWebRutas(body: RequestBody): Flow<NetworkRetrofit<JRuta>>
    suspend fun getWebEncuesta(body: RequestBody): Flow<NetworkRetrofit<JEncuesta>>

    suspend fun getWebPreventa(body: RequestBody): Flow<NetworkRetrofit<JVolumen>>
    suspend fun getWebCobertura(body: RequestBody): Flow<NetworkRetrofit<JCobCart>>
    suspend fun getWebCartera(body: RequestBody): Flow<NetworkRetrofit<JCobCart>>
    suspend fun getWebPedidos(body: RequestBody): Flow<NetworkRetrofit<JPedido>>
    suspend fun getWebCambiosCli(body: RequestBody): Flow<NetworkRetrofit<JCambio>>
    suspend fun getWebCambiosEmp(body: RequestBody): Flow<NetworkRetrofit<JCambio>>
    suspend fun getWebVisicooler(body: RequestBody): Flow<NetworkRetrofit<JVisicooler>>
    suspend fun getWebVisisuper(body: RequestBody): Flow<NetworkRetrofit<JVisisuper>>
    suspend fun getWebUmes(body: RequestBody): Flow<NetworkRetrofit<JUmes>>
    suspend fun getWebSoles(body: RequestBody): Flow<NetworkRetrofit<JSoles>>

    suspend fun getWebUmesGenerico(body: RequestBody): Flow<NetworkRetrofit<JGenerico>>
    suspend fun getWebSolesGenerico(body: RequestBody): Flow<NetworkRetrofit<JGenerico>>
    suspend fun getWebUmesDetalle(body: RequestBody): Flow<NetworkRetrofit<JGenerico>>
    suspend fun getWebSolesDetalle(body: RequestBody): Flow<NetworkRetrofit<JGenerico>>
    suspend fun getWebCoberturaPendiente(body: RequestBody): Flow<NetworkRetrofit<JCoberturados>>
    suspend fun getWebPedidosRealizados(body: RequestBody): Flow<NetworkRetrofit<JPediGen>>

    suspend fun getWebPedimap(body: RequestBody): Flow<NetworkRetrofit<JPedimap>>
    suspend fun getWebBajaVendedor(body: RequestBody): Flow<NetworkRetrofit<JBajaVendedor>>
    suspend fun getWebBajaSupervisor(body: RequestBody): Flow<NetworkRetrofit<JBajaSupervisor>>

    //  Send Server
    suspend fun setWebSeguimiento(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebVisita(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebAlta(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebAltaDatos(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebBaja(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebBajaEstados(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebRespuestas(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun setWebFotos(body: RequestBody): Flow<NetworkRetrofit<JFoto>>
}