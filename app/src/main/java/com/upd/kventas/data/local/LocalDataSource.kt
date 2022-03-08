package com.upd.kventas.data.local

import com.upd.kventas.data.model.*
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

    fun getAltas(): Flow<List<TAlta>> {
        return qdao.getAltas()
    }

    fun getObsDistritos(): Flow<List<Combo>> {
        return qdao.getObsDistritos()
    }

    fun getObsNegocios(): Flow<List<Combo>> {
        return qdao.getObsNegocios()
    }

    fun getBajas(): Flow<List<TBaja>> {
        return qdao.getBajas()
    }

    fun getRowBajas(): Flow<List<RowBaja>> {
        return qdao.getRowBajas()
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

    suspend fun getEncuestas(): List<Encuesta> {
        return qdao.getEncuesta().asEncuestaList()
    }

    suspend fun getDataCliente(cliente: String): List<DataCliente> {
        return qdao.getDataCliente(cliente)
    }

    suspend fun getDataAlta(alta: String): DataCliente {
        return qdao.getDataAlta(alta)
    }

    suspend fun getAltaDatoSpecific(alta: String): TADatos? {
        return qdao.getAltaDatoSpecific(alta)
    }

    suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper {
        return qdao.getBajaSuper(codigo, fecha)
    }

    suspend fun isClienteBaja(cliente: String): Boolean {
        return qdao.getBajaCliente(cliente) != null
    }

    suspend fun getLastAlta(): TAlta? {
        return qdao.getLastAlta()
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

    suspend fun saveEstado(est: TEstado) {
        dao.insertEstado(est)
    }

    suspend fun saveBaja(baja: TBaja) {
        dao.insertBaja(baja)
    }

    suspend fun saveAlta(alta: TAlta) {
        dao.insertAlta(alta)
    }

    suspend fun saveAltaDatos(da: TADatos) {
        dao.insertAltaDatos(da)
    }

    suspend fun saveBajaSuper(baja: List<BajaSupervisor>) {
        dao.insertBajaSupervisor(baja.map { it.asTBajaSuper() })
    }

    suspend fun saveEstadoBaja(estado: TBajaEstado) {
        dao.insertEstadoBaja(estado)
    }

    suspend fun updateLocationAlta(locationAlta: LocationAlta) {
        dao.updateLocationAlta(locationAlta)
    }

    suspend fun updateMiniAlta(mini: MiniUpdAlta) {
        dao.updateMiniAlta(mini)
    }

    suspend fun updateAltaDatos(upd: TADatos) {
        dao.updateAltaDatos(upd)
    }

    suspend fun updateMiniBaja(mini: MiniUpdBaja) {
        dao.updateMiniBaja(mini)
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

    suspend fun deleteEstado() {
        dao.deleteEstado()
    }

    suspend fun deleteBaja() {
        dao.deleteBaja()
    }

    suspend fun deleteAlta() {
        dao.deleteAlta()
    }
    suspend fun deleteAltaDatos() {
        dao.deleteAltaDatos()
    }

    suspend fun deleteBajaSuper() {
        dao.deleteBajaSuper()
    }

    suspend fun deleteEstadoBaja() {
        dao.deleteEstadoBaja()
    }
}