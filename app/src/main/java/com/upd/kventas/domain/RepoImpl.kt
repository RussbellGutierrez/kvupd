package com.upd.kventas.domain

import com.upd.kventas.data.local.LocalDataSource
import com.upd.kventas.data.model.*
import com.upd.kventas.data.remote.WebDataSource
import com.upd.kventas.utils.BaseApiResponse
import com.upd.kventas.utils.Network
import com.upd.kventas.utils.timeToText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val webDataSource: WebDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse(), Repository {
    //Room
    override fun getFlowConfig(): Flow<List<Config>> {
        return localDataSource.getObsConfiguracion().distinctUntilChanged()
    }

    override fun getFlowRowCliente(): Flow<List<RowCliente>> {
        return localDataSource.getRowClientes().distinctUntilChanged()
    }

    override fun getFlowLocation(): Flow<List<TSeguimiento>> {
        return localDataSource.getLastLocation()
    }

    override fun getFlowMarker(): Flow<List<MarkerMap>> {
        return localDataSource.getMarkers()
    }

    override suspend fun getConfig(): List<Config> {
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

    override suspend fun getClienteDetail(cliente: String): List<DataCliente> {
        return localDataSource.getDataCliente(cliente)
    }

    override suspend fun isClienteBaja(cliente: String) =
        localDataSource.isClienteBaja(cliente)

    override suspend fun getStarterTime(): Long? {
        localDataSource.getConfig().forEach { i ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, i.hini.split(":")[0].toInt())
                set(Calendar.MINUTE, i.hini.split(":")[1].toInt())
                set(Calendar.SECOND, i.hini.split(":")[2].toInt())
            }
            if (calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            return calendar.timeInMillis - System.currentTimeMillis()
        }
        return null
    }

    override suspend fun workDay(): Boolean? {
        val time = Calendar.getInstance().time.timeToText(3).replace(":","").toInt()
        localDataSource.getConfig().forEach { i ->
            val inicio = i.hini.replace(":","").toInt()
            val final = i.hfin.replace(":","").toInt()
            return time in inicio..final
        }
        return null
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

    override suspend fun saveDistrito(distrito: List<Combo>) {
        localDataSource.saveDistrito(distrito)
    }

    override suspend fun saveNegocio(negocio: List<Combo>) {
        localDataSource.saveNegocio(negocio)
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

    override suspend fun loginAdministrator(body: RequestBody): Flow<Network<Login>> {
        return flow {
            emit(safeApiCall { webDataSource.loginUser(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun registerWebDevice(body: RequestBody): Flow<Network<JObj>> {
        return flow {
            emit(safeApiCall { webDataSource.registerWebDevice(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebConfiguracion(body: RequestBody): Flow<Network<JConfig>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebConfiguracion(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebClientes(body: RequestBody): Flow<Network<JCliente>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebClientes(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebEmpleados(body: RequestBody): Flow<Network<JVendedores>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebEmpleados(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebDistritos(body: RequestBody): Flow<Network<JCombo>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebDistritos(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebNegocios(body: RequestBody): Flow<Network<JCombo>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebNegocios(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebEncuesta(body: RequestBody): Flow<Network<JEncuesta>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebEncuesta(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPreventa(body: RequestBody): Flow<Network<JVolumen>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPreventa(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCobertura(body: RequestBody): Flow<Network<JCobCart>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCobertura(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCartera(body: RequestBody): Flow<Network<JCobCart>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCartera(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCambiosCli(body: RequestBody): Flow<Network<JCambio>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCambiosCli(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCambiosEmp(body: RequestBody): Flow<Network<JCambio>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCambiosEmp(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedidos(body: RequestBody): Flow<Network<JPedido>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedidos(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebVisicooler(body: RequestBody): Flow<Network<JVisicooler>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebVisicooler(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebVisisuper(body: RequestBody): Flow<Network<JVisisuper>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebVisisuper(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmes(body: RequestBody): Flow<Network<JUmes>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmes(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSoles(body: RequestBody): Flow<Network<JSoles>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSoles(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmesGenerico(body: RequestBody): Flow<Network<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmesGenerico(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSolesGenerico(body: RequestBody): Flow<Network<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSolesGenerico(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebUmesDetalle(body: RequestBody): Flow<Network<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebUmesDetalle(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebSolesDetalle(body: RequestBody): Flow<Network<JGenerico>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebSolesDetalle(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebCoberturaPendiente(body: RequestBody): Flow<Network<JCoberturados>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebCoberturaPendiente(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedidosRealizados(body: RequestBody): Flow<Network<JPediGen>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedidosRealizados(body) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getWebPedimap(body: RequestBody): Flow<Network<JPedimap>> {
        return flow {
            emit(safeApiCall { webDataSource.getWebPedimap(body) })
        }.flowOn(Dispatchers.IO)
    }
}