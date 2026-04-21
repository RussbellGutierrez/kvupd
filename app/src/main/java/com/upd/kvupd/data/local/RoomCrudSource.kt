package com.upd.kvupd.data.local

import com.upd.kvupd.data.local.cache.CacheCrud
import com.upd.kvupd.data.local.core.CoreCrud
import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Configuracion
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
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
    private val coreCrud: CoreCrud,
    private val cacheCrud: CacheCrud
) {
    /////      TRANSACTION
    suspend fun clearSessionData() {
        cacheCrud.clearSessionData()
    }

    suspend fun clearServerUploadData(hoy: String) {
        coreCrud.clearServerUploadData(hoy)
    }

    suspend fun replaceConfiguracion(item: List<Configuracion>) {
        coreCrud.replaceConfiguracion(item.map { it.asTConfig() })
    }

    suspend fun replaceClientes(item: List<Cliente>) {
        cacheCrud.replaceClientes(item.map { it.asTCliente() })
    }

    suspend fun replaceVendedores(item: List<Vendedor>) {
        cacheCrud.replaceVendedores(item.map { it.asTVendedor() })
    }

    suspend fun replaceDistritos(item: List<Distrito>) {
        cacheCrud.replaceDistritos(item.map { it.asTDistrito() })
    }

    suspend fun replaceNegocios(item: List<Negocio>) {
        cacheCrud.replaceNegocios(item.map { it.asTNegocio() })
    }

    suspend fun replaceRutas(item: List<Ruta>) {
        cacheCrud.replaceRutas(item.map { it.asTRutas() })
    }

    suspend fun replaceEncuesta(item: List<Encuesta>) {
        cacheCrud.replaceEncuesta(item.map { it.asTEncuesta() })
    }

    /////      INSERT
    suspend fun apiGuardarConfiguracion(item: List<Configuracion>) {
        coreCrud.insertConfiguracion(item.map { it.asTConfig() })
    }

    suspend fun apiGuardarClientes(item: List<Cliente>) {
        cacheCrud.insertClientes(item.map { it.asTCliente() })
    }

    suspend fun apiGuardarVendedores(item: List<Vendedor>) {
        cacheCrud.insertVendedores(item.map { it.asTVendedor() })
    }

    suspend fun apiGuardarDistrito(item: List<Distrito>) {
        cacheCrud.insertDistritos(item.map { it.asTDistrito() })
    }

    suspend fun apiGuardarNegocio(item: List<Negocio>) {
        cacheCrud.insertNegocios(item.map { it.asTNegocio() })
    }

    suspend fun apiGuardarRutas(item: List<Ruta>) {
        cacheCrud.insertRutas(item.map { it.asTRutas() })
    }

    suspend fun apiGuardarEncuesta(item: List<Encuesta>) {
        cacheCrud.insertEncuestas(item.map { it.asTEncuesta() })
    }

    suspend fun apiGuardarBajaSupervisor(item: List<BajaSupervisor>) {
        cacheCrud.insertBajaSupervisor(item.map { it.asTBajaSuper() })
    }

    suspend fun guardarSeguimiento(item: TableSeguimiento) {
        coreCrud.insertSeguimiento(item)
    }

    suspend fun guardarBajas(item: TableBaja) {
        coreCrud.insertBaja(item)
    }

    suspend fun guardarBajaProcesada(item: TableBajaProcesada) {
        coreCrud.insertBajaProcesada(item)
    }

    suspend fun guardarAltas(item: TableAlta) {
        coreCrud.insertAlta(item)
    }

    suspend fun guardarDatosAlta(item: TableAltaDatos) {
        coreCrud.insertAltaDatos(item)
    }

    suspend fun guardarRespuestas(item: List<TableRespuesta>) {
        coreCrud.insertRespuesta(item)
    }

    suspend fun guardarFoto(item: TableFoto) {
        coreCrud.insertFoto(item)
    }

    /////      UPDATE
    suspend fun actualizarSeguimiento(actual: TableSeguimiento) {
        coreCrud.updateSeguimiento(actual)
    }

    suspend fun actualizarAlta(actual: TableAlta) {
        coreCrud.updateAlta(actual)
    }

    suspend fun actualizarDatosAlta(actual: TableAltaDatos) {
        coreCrud.updateAltaDatos(actual)
    }

    suspend fun actualizarBaja(actual: TableBaja) {
        coreCrud.updateBaja(actual)
    }

    suspend fun actualizarBajaProcesada(actual: TableBajaProcesada) {
        coreCrud.updateBajaProcesada(actual)
    }

    suspend fun actualizarRespuesta(actual: TableRespuesta) {
        coreCrud.updateRespuesta(actual)
    }

    suspend fun actualizarFoto(actual: TableFoto) {
        coreCrud.updateFoto(actual)
    }
}