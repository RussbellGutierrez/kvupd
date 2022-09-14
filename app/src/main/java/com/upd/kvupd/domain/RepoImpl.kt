package com.upd.kvupd.domain

import android.location.Location
import com.upd.kvupd.data.local.LocalDataSource
import com.upd.kvupd.data.model.*
import com.upd.kvupd.data.remote.WebDataSource
import com.upd.kvupd.utils.BaseApiResponse
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.dateToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val webDataSource: WebDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse(), Repository {

    override fun getFlowConfig(): Flow<List<TConfiguracion>> {
        return localDataSource.getObsConfiguracion().distinctUntilChanged()
    }

    override fun getFlowSession(): Flow<TSesion> {
        return localDataSource.getObsSession().distinctUntilChanged()
    }

    override fun getFlowRowCliente(): Flow<List<RowCliente>> {
        return localDataSource.getRowClientes().distinctUntilChanged()
    }

    override fun getFlowLocation(): Flow<List<TSeguimiento>> {
        return localDataSource.getLastLocation().distinctUntilChanged()
    }

    override fun getFlowMarker(observacion: String): Flow<List<MarkerMap>> {
        return localDataSource.getMarkers(observacion).distinctUntilChanged()
    }

    override fun getFlowAltas(): Flow<List<TAlta>> {
        return localDataSource.getAltas().distinctUntilChanged()
    }

    override fun getFlowDistritos(): Flow<List<Combo>> {
        return localDataSource.getObsDistritos().distinctUntilChanged()
    }

    override fun getFlowNegocios(): Flow<List<Combo>> {
        return localDataSource.getObsNegocios().distinctUntilChanged()
    }

    override fun getFlowBajas(): Flow<List<TBaja>> {
        return localDataSource.getBajas().distinctUntilChanged()
    }

    override fun getFlowRowBaja(): Flow<List<RowBaja>> {
        return localDataSource.getRowBajas().distinctUntilChanged()
    }

    override fun getFlowRutas(): Flow<List<TRutas>> {
        return localDataSource.getObsRutas().distinctUntilChanged()
    }

    override fun getFlowIncidencias(): Flow<List<TIncidencia>> {
        return localDataSource.getIncidencias().distinctUntilChanged()
    }

    override suspend fun getSesion(): TSesion? {
        return localDataSource.getSesion()
    }

    override suspend fun getConfig(): TConfiguracion? {
        return localDataSource.getConfig()
    }

    override suspend fun getClientes(): List<Cliente> {
        return localDataSource.getClientes()
    }

    override suspend fun getEmpleados(): List<Vendedor> {
        return localDataSource.getEmpleados()
    }

    override suspend fun getDistritos(): List<Combo> {
        return localDataSource.getDistritos()
    }

    override suspend fun getNegocios(): List<Combo> {
        return localDataSource.getNegocios()
    }

    override suspend fun getRutas(): List<Ruta> {
        return localDataSource.getRutas()
    }

    override suspend fun getListEncuestas(): List<Cabecera> {
        return localDataSource.getCabeceraEncuestas()
    }

    override suspend fun getPreguntas(): List<TEncuesta> {
        return localDataSource.getEncuesta()
    }

    override suspend fun getClienteDetail(cliente: String, observacion: String): List<DataCliente> {
        return localDataSource.getDataCliente(cliente, observacion)
    }

    override suspend fun getDataAlta(alta: String): DataAlta {
        return localDataSource.getDataAlta(alta)
    }

    override suspend fun getAltaDatoSpecific(alta: String): TADatos? {
        return localDataSource.getAltaDatoSpecific(alta)
    }

    override suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper {
        return localDataSource.getBajaSuperSpecific(codigo, fecha)
    }

    override suspend fun isClienteBaja(cliente: String) =
        localDataSource.isClienteBaja(cliente)

    override suspend fun getLastAlta() =
        localDataSource.getLastAlta()

    override suspend fun processAlta(fecha: String, location: Location) {
        val alta = getLastAlta()
        val last = if (alta != null) {
            alta.idaux + 1
        } else {
            "${CONF.codigo}001".toInt()
        }
        val item = TAlta(
            last,
            fecha,
            CONF.codigo,
            location.longitude,
            location.latitude,
            location.accuracy.toDouble(),
            "Pendiente",
            0
        )
        saveAlta(item)
    }

    override suspend fun isDataToday(today: String): Boolean {
        var resp = false
        localDataSource.getConfig()?.let { i ->
            resp = i.fecha == today
        }
        return resp
    }

    override suspend fun getStarterTime(): Long {
        val l: Long
        val config = localDataSource.getConfig()
        val sesion = localDataSource.getSesion()
        val hini = when {
            sesion != null -> sesion.hini
            config != null -> config.hini
            else -> ""
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hini.split(":")[0].toInt())
            set(Calendar.MINUTE, hini.split(":")[1].toInt())
            set(Calendar.SECOND, hini.split(":")[2].toInt())
        }
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        l = calendar.timeInMillis - System.currentTimeMillis()
        return l
    }

    override suspend fun getFinishTime(): Long {
        var l = 0L
        localDataSource.getConfig()?.let { i ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, i.hfin.split(":")[0].toInt())
                set(Calendar.MINUTE, i.hfin.split(":")[1].toInt())
                set(Calendar.SECOND, i.hfin.split(":")[2].toInt())
            }
            l = if (getIntoHours()) {
                calendar.timeInMillis - System.currentTimeMillis()
            }else {
                0
            }
        }
        return l
    }

    override suspend fun getIntoHours(): Boolean {
        var result: Boolean
        localDataSource.getSesion().let {
            result = if (it != null) {

                val d = Calendar.getInstance().time
                val hora = d.dateToday(3).replace(":", "").toInt()
                val inicio = it.hini.replace(":", "").toInt()
                val fin = it.hfin.replace(":", "").toInt()
                hora in inicio..fin
            }else {
                true
            }
        }
        return result
    }

    override suspend fun getSeleccionado(): TEncuestaSeleccionado? {
        return localDataSource.getEncuestaSeleccion()
    }

    override suspend fun clienteRespondioActual(cliente: String) =
        localDataSource.clienteRespondio(cliente)

    override suspend fun clienteRespondioAntes(cliente: String) =
        localDataSource.clienteRespondioHistorico(cliente).encuesta

    override suspend fun saveSesion(config: Config) {
        localDataSource.saveSesion(config)
    }

    override suspend fun saveConfiguracion(config: List<Config>) {
        localDataSource.saveConfiguracion(config)
    }

    override suspend fun saveClientes(cliente: List<Cliente>) {
        localDataSource.saveClientes(cliente)
    }

    override suspend fun saveEmpleados(empleado: List<Vendedor>) {
        localDataSource.saveEmpleados(empleado)
    }

    override suspend fun saveDistritos(distrito: List<Combo>) {
        localDataSource.saveDistrito(distrito)
    }

    override suspend fun saveNegocios(negocio: List<Combo>) {
        localDataSource.saveNegocio(negocio)
    }

    override suspend fun saveRutas(ruta: List<Ruta>) {
        localDataSource.saveRuta(ruta)
    }

    override suspend fun saveEncuesta(encuesta: List<Encuesta>) {
        localDataSource.saveEncuesta(encuesta)
    }

    override suspend fun saveSeguimiento(seguimiento: TSeguimiento) {
        localDataSource.saveSeguimiento(seguimiento)
    }

    override suspend fun saveVisita(visita: TVisita) {
        localDataSource.saveVisita(visita)
    }

    override suspend fun saveEstado(estado: TEstado) {
        localDataSource.saveEstado(estado)
    }

    override suspend fun saveBaja(baja: TBaja) {
        localDataSource.saveBaja(baja)
    }

    override suspend fun saveAlta(alta: TAlta) {
        localDataSource.saveAlta(alta)
    }

    override suspend fun saveAltaDatos(da: TADatos) {
        localDataSource.saveAltaDatos(da)
    }

    override suspend fun saveBajaSuper(baja: List<BajaSupervisor>) {
        localDataSource.saveBajaSuper(baja)
    }

    override suspend fun saveBajaEstado(estado: TBEstado) {
        localDataSource.saveEstadoBaja(estado)
    }

    override suspend fun saveRespuestaOneByOne(respuesta: TRespuesta) {
        localDataSource.saveRespuestaIndividual(respuesta)
    }

    override suspend fun saveFoto(respuesta: TRespuesta) {
        localDataSource.saveFoto(respuesta)
    }

    override suspend fun saveSeleccionado(selec: TEncuestaSeleccionado) {
        localDataSource.saveSeleccionado(selec)
    }

    override suspend fun saveRespuesta(respuesta: List<TRespuesta>) {
        localDataSource.saveRespuesta(respuesta)
    }

    override suspend fun saveIncidencia(respuesta: TIncidencia) {
        localDataSource.saveIncidencia(respuesta)
    }

    override suspend fun getServerSeguimiento(estado: String): List<TSeguimiento> {
        return localDataSource.getServerSeguimiento(estado)
    }

    override suspend fun getServerVisita(estado: String): List<TVisita> {
        return localDataSource.getServerVisita(estado)
    }

    override suspend fun getServerAlta(estado: String): List<TAlta> {
        return localDataSource.getServerAlta(estado)
    }

    override suspend fun getServerAltadatos(estado: String): List<TADatos> {
        return localDataSource.getServerAltadatos(estado)
    }

    override suspend fun getServerBaja(estado: String): List<TBaja> {
        return localDataSource.getServerBaja(estado)
    }

    override suspend fun getServerBajaestado(estado: String): List<TBEstado> {
        return localDataSource.getServerBajaestado(estado)
    }

    override suspend fun getServerRespuesta(estado: String): List<TRespuesta> {
        return localDataSource.getServerRespuesta(estado)
    }

    override suspend fun getServerFoto(estado: String): List<TRespuesta> {
        return localDataSource.getServerFoto(estado)
    }

    override suspend fun updateLocationAlta(locationAlta: LocationAlta) {
        localDataSource.updateLocationAlta(locationAlta)
    }

    override suspend fun updateMiniAlta(miniUpdAlta: MiniUpdAlta) {
        localDataSource.updateMiniAlta(miniUpdAlta)
    }

    override suspend fun updateAltaDatos(upd: TADatos) {
        localDataSource.updateAltaDatos(upd)
    }

    override suspend fun updateMiniBaja(miniUpdBaja: MiniUpdBaja) {
        localDataSource.updateMiniBaja(miniUpdBaja)
    }

    override suspend fun deleteConfig() {
        localDataSource.deleteConfig()
    }

    override suspend fun deleteClientes() {
        localDataSource.deleteCliente()
    }

    override suspend fun deleteEmpleados() {
        localDataSource.deleteEmpleado()
    }

    override suspend fun deleteDistritos() {
        localDataSource.deleteDistrito()
    }

    override suspend fun deleteNegocios() {
        localDataSource.deleteNegocio()
    }

    override suspend fun deleteRutas() {
        localDataSource.deleteRuta()
    }

    override suspend fun deleteEncuesta() {
        localDataSource.deleteEncuesta()
    }

    override suspend fun deleteSeguimiento() {
        localDataSource.deleteSeguimiento()
    }

    override suspend fun deleteVisita() {
        localDataSource.deleteVisita()
    }

    override suspend fun deleteEstado() {
        localDataSource.deleteEstado()
    }

    override suspend fun deleteBaja() {
        localDataSource.deleteBaja()
    }

    override suspend fun deleteAlta() {
        localDataSource.deleteAlta()
    }

    override suspend fun deleteAltaDatos() {
        localDataSource.deleteAltaDatos()
    }

    override suspend fun deleteBajaSuper() {
        localDataSource.deleteBajaSuper()
    }

    override suspend fun deleteBajaEstado() {
        localDataSource.deleteEstadoBaja()
    }

    override suspend fun deleteSeleccionado() {
        localDataSource.deleteSeleccionado()
    }

    override suspend fun deleteRespuesta() {
        localDataSource.deleteRespuesta()
    }

    override suspend fun deleteIncidencia() {
        localDataSource.deleteIncidencia()
    }

    override suspend fun loginAdministrator(body: RequestBody): Flow<NetworkRetrofit<Login>> {
        return flow {
            emit(safeApiCall { webDataSource.loginUser(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun registerWebDevice(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.registerWebDevice(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebConfiguracion(body: RequestBody): Flow<NetworkRetrofit<JConfig>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebConfiguracion(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebClientes(body: RequestBody): Flow<NetworkRetrofit<JCliente>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebClientes(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebEmpleados(body: RequestBody): Flow<NetworkRetrofit<JVendedores>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebEmpleados(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebDistritos(body: RequestBody): Flow<NetworkRetrofit<JCombo>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebDistritos(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebNegocios(body: RequestBody): Flow<NetworkRetrofit<JCombo>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebNegocios(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebRutas(body: RequestBody): Flow<NetworkRetrofit<JRuta>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebRutas(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebEncuesta(body: RequestBody): Flow<NetworkRetrofit<JEncuesta>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebEncuesta(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPreventa(body: RequestBody): Flow<NetworkRetrofit<JVolumen>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPreventa(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCobertura(body: RequestBody): Flow<NetworkRetrofit<JCobCart>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCobertura(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCartera(body: RequestBody): Flow<NetworkRetrofit<JCobCart>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCartera(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCambiosCli(body: RequestBody): Flow<NetworkRetrofit<JCambio>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCambiosCli(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCambiosEmp(body: RequestBody): Flow<NetworkRetrofit<JCambio>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCambiosEmp(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedidos(body: RequestBody): Flow<NetworkRetrofit<JPedido>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedidos(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebVisicooler(body: RequestBody): Flow<NetworkRetrofit<JVisicooler>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebVisicooler(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebVisisuper(body: RequestBody): Flow<NetworkRetrofit<JVisisuper>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebVisisuper(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmes(body: RequestBody): Flow<NetworkRetrofit<JUmes>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmes(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSoles(body: RequestBody): Flow<NetworkRetrofit<JSoles>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSoles(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmesGenerico(body: RequestBody): Flow<NetworkRetrofit<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmesGenerico(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSolesGenerico(body: RequestBody): Flow<NetworkRetrofit<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSolesGenerico(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmesDetalle(body: RequestBody): Flow<NetworkRetrofit<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmesDetalle(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSolesDetalle(body: RequestBody): Flow<NetworkRetrofit<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSolesDetalle(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCoberturaPendiente(body: RequestBody): Flow<NetworkRetrofit<JCoberturados>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCoberturaPendiente(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedidosRealizados(body: RequestBody): Flow<NetworkRetrofit<JPediGen>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedidosRealizados(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedimap(body: RequestBody): Flow<NetworkRetrofit<JPedimap>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedimap(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebBajaVendedor(body: RequestBody): Flow<NetworkRetrofit<JBajaVendedor>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebBajaVendedor(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebBajaSupervisor(body: RequestBody): Flow<NetworkRetrofit<JBajaSupervisor>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebBajaSupervisor(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebSeguimiento(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerSeguimiento(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebVisita(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerVisita(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebAlta(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerAlta(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebAltaDatos(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerAltaDatos(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebBaja(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerBaja(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebBajaEstados(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerBajaEstados(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebRespuestas(body: RequestBody): Flow<NetworkRetrofit<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerRespuestas(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun setWebFotos(body: RequestBody): Flow<NetworkRetrofit<JFoto>> {
        return flow {
            emit(safeApiCall { webDataSource.setServerFotos(body) })
        }.flowOn(Dispatchers.IO)
    }
}