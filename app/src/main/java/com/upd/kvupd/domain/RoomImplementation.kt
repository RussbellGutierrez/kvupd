package com.upd.kvupd.domain

import com.upd.kvupd.data.local.RoomCrudSource
import com.upd.kvupd.data.local.RoomQuerySource
import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Configuracion
import com.upd.kvupd.data.model.Consulta
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableEstado
import com.upd.kvupd.data.model.TableIncidencia
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.TableVendedor
import com.upd.kvupd.data.model.Vendedor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomImplementation @Inject constructor(
    private val crudSource: RoomCrudSource,
    private val querySource: RoomQuerySource
) : RoomFunctions {
    override suspend fun apiSaveConfiguracion(item: List<Configuracion>) {
        crudSource.apiGuardarConfiguracion(item)
    }

    override suspend fun apiSaveClientes(item: List<Cliente>) {
        crudSource.apiGuardarClientes(item)
    }

    override suspend fun apiSaveVendedores(item: List<Vendedor>) {
        crudSource.apiGuardarVendedores(item)
    }

    override suspend fun apiSaveDistritos(item: List<Distrito>) {
        crudSource.apiGuardarDistrito(item)
    }

    override suspend fun apiSaveNegocios(item: List<Negocio>) {
        crudSource.apiGuardarNegocio(item)
    }

    override suspend fun apiSaveRutas(item: List<Ruta>) {
        crudSource.apiGuardarRutas(item)
    }

    override suspend fun apiSaveEncuesta(item: List<Encuesta>) {
        crudSource.apiGuardarEncuesta(item)
    }

    override suspend fun apiSaveBajaSupervisor(item: List<BajaSupervisor>) {
        crudSource.apiGuardarBajaSupervisor(item)
    }

    override suspend fun apiSaveConsulta(item: List<Consulta>) {
        crudSource.apiGuardarConsulta(item)
    }

    override suspend fun saveSeguimiento(item: TableSeguimiento) {
        crudSource.guardarSeguimiento(item)
    }

    override suspend fun saveEstado(item: TableEstado) {
        crudSource.guardarEstado(item)
    }

    override suspend fun saveBaja(item: TableBaja) {
        crudSource.guardarBajas(item)
    }

    override suspend fun saveAlta(item: TableAlta) {
        crudSource.guardarAltas(item)
    }

    override suspend fun saveDatosAlta(item: TableAltaDatos) {
        crudSource.guardarDatosAlta(item)
    }

    override suspend fun saveRespuestas(item: List<TableRespuesta>) {
        crudSource.guardarRespuestas(item)
    }

    override suspend fun saveIncidencia(item: TableIncidencia) {
        crudSource.guardarIncidencias(item)
    }

    override suspend fun updateSeguimiento(actual: TableSeguimiento) {
        crudSource.actualizarSeguimiento(actual)
    }

    override suspend fun updateAlta(actual: TableAlta) {
        crudSource.actualizarAlta(actual)
    }

    override suspend fun updateDatosAlta(actual: TableAltaDatos) {
        crudSource.actualizarDatosAlta(actual)
    }

    override suspend fun updateBaja(actual: TableBaja) {
        crudSource.actualizarBaja(actual)
    }

    override suspend fun updateRespuesta(actual: TableRespuesta) {
        crudSource.actualizarRespuesta(actual)
    }

    override suspend fun deleteConfiguracion() {
        crudSource.borrarConfiguracion()
    }

    override suspend fun deleteClientes() {
        crudSource.borrarClientes()
    }

    override suspend fun deleteVendedores() {
        crudSource.borrarVendedores()
    }

    override suspend fun deleteDistritos() {
        crudSource.borrarDistritos()
    }

    override suspend fun deleteNegocios() {
        crudSource.borrarNegocios()
    }

    override suspend fun deleteRutas() {
        crudSource.borrarRutas()
    }

    override suspend fun deleteEncuesta() {
        crudSource.borrarEncuesta()
    }

    override suspend fun deleteConsultas() {
        crudSource.borrarConsultas()
    }

    override suspend fun deleteSeguimiento() {
        crudSource.borrarSeguimiento()
    }

    override suspend fun deleteEstados() {
        crudSource.borrarEstados()
    }

    override suspend fun deleteBajas() {
        crudSource.borrarBaja()
    }

    override suspend fun deleteAltas() {
        crudSource.borrarAlta()
    }

    override suspend fun deleteDatosAltas() {
        crudSource.borrarDatosAlta()
    }

    override suspend fun deleteBajasSupervisor() {
        crudSource.borrarBajaSupervisor()
    }

    override suspend fun deleteRespuestas() {
        crudSource.borrarRespuestas()
    }

    override suspend fun deleteIncidencias() {
        crudSource.borrarIncidencias()
    }

    override suspend fun queryConfiguracion(): TableConfiguracion? {
        return querySource.roomConfiguracion()
    }

    override suspend fun queryClientes(): List<TableCliente> {
        return querySource.roomClientes()
    }

    override suspend fun queryVendedores(): List<TableVendedor> {
        return querySource.roomVendedores()
    }

    override suspend fun queryDistritos(): List<TableDistrito> {
        return querySource.roomDistritos()
    }

    override suspend fun queryNegocios(): List<TableNegocio> {
        return querySource.roomNegocios()
    }

    override suspend fun queryRutas(): List<TableRuta> {
        return querySource.roomRutas()
    }

    override suspend fun queryEncuestas(): List<TableEncuesta> {
        return querySource.roomEncuestas()
    }

    override suspend fun queryBajaEspecifica(cliente: String): TableBaja? {
        return querySource.roomBajaEspecifica(cliente)
    }

    override suspend fun listFlowConfiguracion(): Flow<List<TableConfiguracion>> {
        return querySource.flowConfiguracion()
    }

    override suspend fun listFlowClientes(): Flow<List<FlowCliente>> {
        return querySource.flowClientes()
    }

    override suspend fun listFlowAltas(): Flow<List<TableAlta>> {
        return querySource.flowAltas()
    }

    override suspend fun apiServerSeguimiento(sync: Boolean): List<TableSeguimiento> {
        return querySource.serverSeguimiento(sync)
    }

    override suspend fun apiServerAltas(sync: Boolean): List<TableAlta> {
        return querySource.serverAltas(sync)
    }

    override suspend fun apiServerAltaDatos(sync: Boolean): List<TableAltaDatos> {
        return querySource.serverAltaDatos(sync)
    }

    override suspend fun apiServerBajas(sync: Boolean): List<TableBaja> {
        return querySource.serverBajas(sync)
    }

    override suspend fun apiServerEstados(sync: Boolean): List<TableEstado> {
        return querySource.serverEstados(sync)
    }

    override suspend fun apiServerRespuestas(sync: Boolean): List<TableRespuesta> {
        return querySource.serverRespuestas(sync)
    }

    override suspend fun apiServerFotos(sync: Boolean): List<TableRespuesta> {
        return querySource.serverFotos(sync)
    }
}