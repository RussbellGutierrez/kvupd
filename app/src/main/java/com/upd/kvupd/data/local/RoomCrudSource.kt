package com.upd.kvupd.data.local

import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Configuracion
import com.upd.kvupd.data.model.Consulta
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableEstado
import com.upd.kvupd.data.model.TableIncidencia
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.Vendedor
import com.upd.kvupd.data.model.asTBajaSuper
import com.upd.kvupd.data.model.asTCliente
import com.upd.kvupd.data.model.asTConfig
import com.upd.kvupd.data.model.asTConsulta
import com.upd.kvupd.data.model.asTDistrito
import com.upd.kvupd.data.model.asTEncuesta
import com.upd.kvupd.data.model.asTNegocio
import com.upd.kvupd.data.model.asTRutas
import com.upd.kvupd.data.model.asTVendedor
import javax.inject.Inject

class RoomCrudSource @Inject constructor(
    private val crud: Crud
) {
    /////      INSERT
    suspend fun apiGuardarConfiguracion(item: List<Configuracion>) {
        crud.insertConfiguracion(item.map { it.asTConfig() })
    }

    suspend fun apiGuardarClientes(item: List<Cliente>) {
        crud.insertClientes(item.map { it.asTCliente() })
    }

    suspend fun apiGuardarVendedores(item: List<Vendedor>) {
        crud.insertVendedores(item.map { it.asTVendedor() })
    }

    suspend fun apiGuardarDistrito(item: List<Distrito>) {
        crud.insertDistritos(item.map { it.asTDistrito() })
    }

    suspend fun apiGuardarNegocio(item: List<Negocio>) {
        crud.insertNegocios(item.map { it.asTNegocio() })
    }

    suspend fun apiGuardarRutas(item: List<Ruta>) {
        crud.insertRutas(item.map { it.asTRutas() })
    }

    suspend fun apiGuardarEncuesta(item: List<Encuesta>) {
        crud.insertEncuestas(item.map { it.asTEncuesta() })
    }

    suspend fun apiGuardarBajaSupervisor(item: List<BajaSupervisor>) {
        crud.insertBajaSupervisor(item.map { it.asTBajaSuper() })
    }

    suspend fun apiGuardarConsulta(item: List<Consulta>) {
        crud.insertConsulta(item.map { it.asTConsulta() })
    }

    suspend fun guardarSeguimiento(item: TableSeguimiento) {
        crud.insertSeguimiento(item)
    }

    suspend fun guardarEstado(item: TableEstado) {
        crud.insertEstado(item)
    }

    suspend fun guardarBajas(item: TableBaja) {
        crud.insertBaja(item)
    }

    suspend fun guardarAltas(item: TableAlta) {
        crud.insertAlta(item)
    }

    suspend fun guardarDatosAlta(item: TableAltaDatos) {
        crud.insertAltaDatos(item)
    }

    suspend fun guardarRespuestas(item: List<TableRespuesta>) {
        crud.insertRespuesta(item)
    }

    suspend fun guardarIncidencias(item: TableIncidencia) {
        crud.insertIncidencia(item)
    }

    /////      UPDATE
    suspend fun actualizarSeguimiento(actual: TableSeguimiento) {
        crud.updateSeguimiento(actual)
    }

    suspend fun actualizarAlta(actual: TableAlta) {
        crud.updateAlta(actual)
    }

    suspend fun actualizarDatosAlta(actual: TableAltaDatos) {
        crud.updateAltaDatos(actual)
    }

    suspend fun actualizarBaja(actual: TableBaja) {
        crud.updateBaja(actual)
    }

    suspend fun actualizarRespuesta(actual: TableRespuesta){
        crud.updateRespuesta(actual)
    }

    /////      DELETE
    suspend fun borrarConfiguracion(){
        crud.deleteConfiguracion()
    }

    suspend fun borrarClientes(){
        crud.deleteClientes()
    }

    suspend fun borrarVendedores(){
        crud.deleteVendedores()
    }

    suspend fun borrarDistritos(){
        crud.deleteDistritos()
    }

    suspend fun borrarNegocios(){
        crud.deleteNegocios()
    }

    suspend fun borrarRutas(){
        crud.deleteRutas()
    }

    suspend fun borrarEncuesta(){
        crud.deleteEncuesta()
    }

    suspend fun borrarConsultas(){
        crud.deleteConsulta()
    }

    suspend fun borrarSeguimiento(){
        crud.deleteSeguimiento()
    }

    suspend fun borrarEstados(){
        crud.deleteEstado()
    }

    suspend fun borrarBaja(){
        crud.deleteBaja()
    }

    suspend fun borrarAlta(){
        crud.deleteAlta()
    }

    suspend fun borrarDatosAlta(){
        crud.deleteAltaDatos()
    }

    suspend fun borrarBajaSupervisor(){
        crud.deleteBajaSupervisor()
    }

    suspend fun borrarRespuestas(){
        crud.deleteRespuesta()
    }

    suspend fun borrarIncidencias(){
        crud.deleteIncidencia()
    }
}