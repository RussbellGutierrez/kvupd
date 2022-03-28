package com.upd.kvupd.data.local

import com.upd.kvupd.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: AppDAO, private val qdao: QueryDAO) {

    fun getObsConfiguracion(): Flow<List<TConfiguracion>> {
        return qdao.getObsConfig()
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

    fun getObsRutas(): Flow<List<TRutas>> {
        return qdao.getObsRutas()
    }

    suspend fun getSesion(): TSesion? {
        return qdao.getSesion()
    }

    suspend fun getConfig(): TConfiguracion? {
        return qdao.getConfig()
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

    suspend fun getRutas(): List<Ruta> {
        return qdao.getRutas().asRutaList()
    }

    suspend fun getCabeceraEncuestas(): List<Cabecera> {
        return qdao.getCabeEncuesta()
    }

    suspend fun getEncuesta(): List<TEncuesta> {
        return qdao.getEncuesta()
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

    suspend fun isClienteBaja(cliente: String): Boolean =
        qdao.getBajaCliente(cliente) != null

    suspend fun getLastAlta(): TAlta? {
        return qdao.getLastAlta()
    }

    suspend fun isEncuestaSeleccion(): Boolean =
        qdao.getSeleccionado() != null

    suspend fun saveSesion(config: Config) {
        dao.insertSesion(config.asTSesion())
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

    suspend fun saveRuta(rut: List<Ruta>) {
        dao.insertRut(rut.map { it.asTRutas() })
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

    suspend fun saveEstadoBaja(estado: TBEstado) {
        dao.insertEstadoBaja(estado)
    }

    suspend fun saveSeleccionado(selec: TEncuestaSeleccionado) {
        dao.insertSeleccionado(selec)
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

    suspend fun deleteConfig() {
        dao.deleteConfig()
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

    suspend fun deleteRuta() {
        dao.deleteRutas()
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

    suspend fun deleteSeleccionado() {
        dao.deleteSeleccionado()
    }

    suspend fun getServerSeguimiento(estado: String): List<TSeguimiento> {
        return qdao.seguimientoServer(estado)
    }

    suspend fun getServerVisita(estado: String): List<TVisita> {
        return qdao.visitaServer(estado)
    }

    suspend fun getServerAlta(estado: String): List<TAlta> {
        return qdao.altaServer(estado)
    }

    suspend fun getServerAltadatos(estado: String): List<TADatos> {
        return qdao.altadatosServer(estado)
    }

    suspend fun getServerBaja(estado: String): List<TBaja> {
        return qdao.bajaServer(estado)
    }

    suspend fun getServerBajaestado(estado: String): List<TBEstado> {
        return qdao.bajaestadoServer(estado)
    }
}