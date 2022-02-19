package com.upd.kv.data.local

import com.upd.kv.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: AppDAO, private val qdao: QueryDAO) {

    fun getObsConfiguracion(): Flow<List<Config>> {
        return qdao.getObsConfig().map { it.asConfigList() }
    }

    fun getRowClientes(): Flow<List<RowCliente>> {
        return qdao.getRowClientes()
    }

    fun getLastLocation(): Flow<List<TSeguimiento>> {
        return qdao.getLastLocation()
    }

    fun getMarkers(): Flow<List<MarkerMap>> {
        return qdao.getMarkers()
    }

    suspend fun getConfig(): List<Config> {
        return qdao.getConfig().asConfigList()
    }

    suspend fun getClientes(): List<Cliente> {
        return qdao.getClientes().asClienteList()
    }

    suspend fun getEmpleados(): List<Vendedor> {
        return qdao.getEmpleados().asEmpleadoList()
    }

    suspend fun getDistritos(): List<Combo> {
        return qdao.getDistrito().asDistritoList()
    }

    suspend fun getNegocios(): List<Combo> {
        return qdao.getNegocio().asNegocioList()
    }

    suspend fun getDataCliente(cliente: String): List<DataCliente> {
        return qdao.getDataCliente(cliente)
    }

    suspend fun saveConfiguracion(conf: List<Config>) {
        dao.insertConf(conf.map { it.asTConfig() })
    }

    suspend fun saveClientes(cli: List<Cliente>) {
        dao.insertCli(cli.map { it.asTCliente() })
    }

    suspend fun saveEmpleados(emp: List<Vendedor>) {
        dao.insertEmp(emp.map { it.asTEmpleado() })
    }

    suspend fun saveDistrito(dis: List<Combo>) {
        dao.insertDist(dis.map { it.asTDistrito() })
    }

    suspend fun saveNegocio(neg: List<Combo>) {
        dao.insertNeg(neg.map { it.asTNegocio() })
    }

    suspend fun saveEncuesta(enc: List<Encuesta>) {
        dao.insertEnc(enc.map { it.asTEncuesta() })
    }

    suspend fun saveSeguimiento(seg: TSeguimiento) {
        dao.insertSeguimiento(seg)
    }

    suspend fun saveVisita(vis: TVisita) {
        dao.insertVisita(vis)
    }

    suspend fun deleteCliente() {
        dao.deleteClientes()
    }

    suspend fun deleteEmpleado() {
        dao.deleteEmpleado()
    }

    suspend fun deleteDistrito() {
        dao.deleteDistrito()
    }

    suspend fun deleteNegocio() {
        dao.deleteNegocio()
    }

    suspend fun deleteEncuesta() {
        dao.deleteEncuesta()
    }

    suspend fun deleteSeguimiento() {
        dao.deleteSeguimiento()
    }

    suspend fun deleteVisita() {
        dao.deleteVisita()
    }
}