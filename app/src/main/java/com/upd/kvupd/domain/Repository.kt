package com.upd.kvupd.domain

import android.location.Location
import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cabecera
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Config
import com.upd.kvupd.data.model.Consulta
import com.upd.kvupd.data.model.DataAlta
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.JBajaSupervisor
import com.upd.kvupd.data.model.JBajaVendedor
import com.upd.kvupd.data.model.JCambio
import com.upd.kvupd.data.model.JCliente
import com.upd.kvupd.data.model.JCobCart
import com.upd.kvupd.data.model.JCoberturados
import com.upd.kvupd.data.model.JConfig
import com.upd.kvupd.data.model.JConsulta
import com.upd.kvupd.data.model.JDetCob
import com.upd.kvupd.data.model.JDistrito
import com.upd.kvupd.data.model.JEncuesta
import com.upd.kvupd.data.model.JFoto
import com.upd.kvupd.data.model.JGenerico
import com.upd.kvupd.data.model.JNegocio
import com.upd.kvupd.data.model.JObj
import com.upd.kvupd.data.model.JPediGen
import com.upd.kvupd.data.model.JPedido
import com.upd.kvupd.data.model.JPedimap
import com.upd.kvupd.data.model.JRuta
import com.upd.kvupd.data.model.JSoles
import com.upd.kvupd.data.model.JUmes
import com.upd.kvupd.data.model.JVendedores
import com.upd.kvupd.data.model.JVisicooler
import com.upd.kvupd.data.model.JVisisuper
import com.upd.kvupd.data.model.JVolumen
import com.upd.kvupd.data.model.LocationAlta
import com.upd.kvupd.data.model.Login
import com.upd.kvupd.data.model.MarkerMap
import com.upd.kvupd.data.model.MiniUpdAlta
import com.upd.kvupd.data.model.MiniUpdBaja
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.RowBaja
import com.upd.kvupd.data.model.RowCliente
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.TAAux
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.data.model.TConfiguracion
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.data.model.TEstado
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TSesion
import com.upd.kvupd.data.model.TVisita
import com.upd.kvupd.data.model.Vendedor
import com.upd.kvupd.utils.NetworkRetrofit
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface Repository {
    //  Room Functions
    fun getFlowConfig(): Flow<List<TConfiguracion>>
    fun getFlowSession(): Flow<TSesion>
    fun getFlowRowCliente(): Flow<List<RowCliente>>
    fun getFlowLocation(): Flow<List<TSeguimiento>?>
    fun getFlowMarker(observacion: String): Flow<List<MarkerMap>>
    fun getFlowAltas(): Flow<List<TAlta>>
    fun getFlowDistritos(): Flow<List<Distrito>>
    fun getFlowNegocios(): Flow<List<Negocio>>
    fun getFlowBajas(): Flow<List<TBaja>>
    fun getFlowRowBaja(): Flow<List<RowBaja>>
    fun getFlowRutas(): Flow<List<TRutas>>
    fun getFlowIncidencias(): Flow<List<TIncidencia>>

    suspend fun getSesion(): TSesion?
    suspend fun getConfig(): TConfiguracion?
    suspend fun getClientes(): List<Cliente>
    suspend fun getEmpleados(): List<Vendedor>
    suspend fun getDistritos(): List<Distrito>
    suspend fun getNegocios(): List<Negocio>
    suspend fun getRutas(): List<Ruta>
    suspend fun getListEncuestas(): List<Cabecera>
    suspend fun getPreguntas(): List<TEncuesta>
    suspend fun getConsultaCliente(numero: String, nombre: String): List<TConsulta>
    suspend fun getClienteDetail(cliente: String, observacion: String): List<DataCliente>
    suspend fun getDataAlta(alta: String): DataAlta
    suspend fun getAltaDatoSpecific(alta: String): TADatos?
    suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper
    suspend fun isClienteBaja(cliente: String): Boolean
    suspend fun getLastAux(): Int?
    suspend fun processAlta(fecha: String, location: Location)
    suspend fun processAAux(codigo: Int)
    suspend fun isDataToday(): Int
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
    suspend fun saveDistritos(distrito: List<Distrito>)
    suspend fun saveRutas(ruta: List<Ruta>)
    suspend fun saveNegocios(negocio: List<Negocio>)
    suspend fun saveEncuesta(encuesta: List<Encuesta>)
    suspend fun saveConsulta(consulta: List<Consulta>)
    suspend fun saveSeguimiento(seguimiento: TSeguimiento)
    suspend fun saveVisita(visita: TVisita)
    suspend fun saveEstado(estado: TEstado)
    suspend fun saveBaja(baja: TBaja)
    suspend fun saveAlta(alta: TAlta)
    suspend fun saveAltaDatos(da: TADatos)
    suspend fun saveAAux(aux: TAAux)
    suspend fun saveBajaSuper(baja: List<BajaSupervisor>)
    suspend fun saveBajaEstado(estado: TBEstado)
    suspend fun saveRespuestaOneByOne(respuesta: TRespuesta)
    suspend fun saveFoto(respuesta: TRespuesta)
    suspend fun saveSeleccionado(selec: TEncuestaSeleccionado)
    suspend fun saveRespuesta(respuesta: List<TRespuesta>)
    suspend fun saveIncidencia(respuesta: TIncidencia)
    suspend fun saveAltaFoto(respuesta: TAFoto)

    suspend fun getServerSeguimiento(estado: String): List<TSeguimiento>
    suspend fun getServerVisita(estado: String): List<TVisita>
    suspend fun getServerAlta(estado: String): List<TAlta>
    suspend fun getServerAltadatos(estado: String): List<TADatos>
    suspend fun getServerBaja(estado: String): List<TBaja>
    suspend fun getServerBajaestado(estado: String): List<TBEstado>
    suspend fun getServerRespuesta(estado: String): List<TRespuesta>
    suspend fun getServerFoto(estado: String): List<TRespuesta>
    suspend fun getServerAltaFoto(estado: String): List<TAFoto>

    suspend fun updateSeguimiento(coordenada: TSeguimiento)
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
    suspend fun deleteConsulta()
    suspend fun deleteSeguimiento()
    suspend fun deleteVisita()
    suspend fun deleteEstado()
    suspend fun deleteBaja()
    suspend fun deleteAlta()
    suspend fun deleteAltaDatos()
    suspend fun deleteBajaSuper()
    suspend fun deleteBajaEstado()
    suspend fun deleteEncuestaSeleccionado()
    suspend fun deleteRespuesta()
    suspend fun deleteIncidencia()
    suspend fun deleteAFoto()
    suspend fun deleteAAux()

    //  Retrofit Functions
    suspend fun loginAdministrator(body: RequestBody): Flow<NetworkRetrofit<Login>>
    suspend fun registerWebDevice(body: RequestBody): Flow<NetworkRetrofit<JObj>>
    suspend fun getWebConfiguracion(body: RequestBody): Flow<NetworkRetrofit<JConfig>>
    suspend fun getWebClientes(body: RequestBody): Flow<NetworkRetrofit<JCliente>>
    suspend fun getWebEmpleados(body: RequestBody): Flow<NetworkRetrofit<JVendedores>>
    suspend fun getWebDistritos(body: RequestBody): Flow<NetworkRetrofit<JDistrito>>
    suspend fun getWebNegocios(body: RequestBody): Flow<NetworkRetrofit<JNegocio>>
    suspend fun getWebRutas(body: RequestBody): Flow<NetworkRetrofit<JRuta>>
    suspend fun getWebEncuesta(body: RequestBody): Flow<NetworkRetrofit<JEncuesta>>
    suspend fun getWebConsulta(body: RequestBody): Flow<NetworkRetrofit<JConsulta>>

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
    suspend fun getWebCoberturaDetalle(body: RequestBody): Flow<NetworkRetrofit<JDetCob>>
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
    suspend fun setWebAltaFotos(body: RequestBody): Flow<NetworkRetrofit<JFoto>>
}