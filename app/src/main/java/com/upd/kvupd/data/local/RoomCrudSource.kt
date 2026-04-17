package com.upd.kvupd.data.local

import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Configuracion
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.Vendedor
import com.upd.kvupd.data.model.asTBajaSuper
import com.upd.kvupd.data.model.asTCliente
import com.upd.kvupd.data.model.asTConfig
import com.upd.kvupd.data.model.asTDistrito
import com.upd.kvupd.data.model.asTEncuesta
import com.upd.kvupd.data.model.asTNegocio
import com.upd.kvupd.data.model.asTRutas
import com.upd.kvupd.data.model.asTVendedor
import javax.inject.Inject

class RoomCrudSource @Inject constructor(
    private val crud: Crud
) {
    /////      TRANSACTION
    suspend fun clearSessionData() {
        crud.clearSessionData()
    }

    suspend fun clearServerUploadData(hoy: String) {
        crud.clearServerUploadData(hoy)
    }

    suspend fun replaceConfiguracion(item: List<Configuracion>) {
        crud.replaceConfiguracion(item.map { it.asTConfig() })
    }

    suspend fun replaceClientes(item: List<Cliente>) {
        crud.replaceClientes(item.map { it.asTCliente() })
    }

    suspend fun replaceVendedores(item: List<Vendedor>) {
        crud.replaceVendedores(item.map { it.asTVendedor() })
    }

    suspend fun replaceDistritos(item: List<Distrito>) {
        crud.replaceDistritos(item.map { it.asTDistrito() })
    }

    suspend fun replaceNegocios(item: List<Negocio>) {
        crud.replaceNegocios(item.map { it.asTNegocio() })
    }

    suspend fun replaceRutas(item: List<Ruta>) {
        crud.replaceRutas(item.map { it.asTRutas() })
    }

    suspend fun replaceEncuesta(item: List<Encuesta>) {
        crud.replaceEncuesta(item.map { it.asTEncuesta() })
    }

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

    suspend fun guardarSeguimiento(item: TableSeguimiento) {
        crud.insertSeguimiento(item)
    }

    suspend fun guardarBajas(item: TableBaja) {
        crud.insertBaja(item)
    }

    suspend fun guardarBajaProcesada(item: TableBajaProcesada) {
        crud.insertBajaProcesada(item)
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

    suspend fun guardarFoto(item: TableFoto) {
        crud.insertFoto(item)
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

    suspend fun actualizarBajaProcesada(actual: TableBajaProcesada) {
        crud.updateBajaProcesada(actual)
    }

    suspend fun actualizarRespuesta(actual: TableRespuesta) {
        crud.updateRespuesta(actual)
    }

    suspend fun actualizarFoto(actual: TableFoto) {
        crud.updateFoto(actual)
    }
}