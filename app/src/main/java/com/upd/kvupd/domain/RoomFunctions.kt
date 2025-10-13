package com.upd.kvupd.domain

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

interface RoomFunctions {
    ///     Room Api
    suspend fun apiSaveConfiguracion(item: List<Configuracion>)
    suspend fun apiSaveClientes(item: List<Cliente>)
    suspend fun apiSaveVendedores(item: List<Vendedor>)
    suspend fun apiSaveDistritos(item: List<Distrito>)
    suspend fun apiSaveNegocios(item: List<Negocio>)
    suspend fun apiSaveRutas(item: List<Ruta>)
    suspend fun apiSaveEncuesta(item: List<Encuesta>)
    suspend fun apiSaveBajaSupervisor(item: List<BajaSupervisor>)
    suspend fun apiSaveConsulta(item: List<Consulta>)

    ///     Room Insert
    suspend fun saveSeguimiento(item: TableSeguimiento)
    suspend fun saveEstado(item: TableEstado)
    suspend fun saveBaja(item: TableBaja)
    suspend fun saveAlta(item: TableAlta)
    suspend fun saveDatosAlta(item: TableAltaDatos)
    suspend fun saveRespuestas(item: List<TableRespuesta>)
    suspend fun saveIncidencia(item: TableIncidencia)

    ///     Room Update
    suspend fun updateSeguimiento(actual: TableSeguimiento)
    suspend fun updateAlta(actual: TableAlta)
    suspend fun updateDatosAlta(actual: TableAltaDatos)
    suspend fun updateBaja(actual: TableBaja)
    suspend fun updateRespuesta(actual: TableRespuesta)

    ///     Room Delete
    suspend fun deleteConfiguracion()
    suspend fun deleteClientes()
    suspend fun deleteVendedores()
    suspend fun deleteDistritos()
    suspend fun deleteNegocios()
    suspend fun deleteRutas()
    suspend fun deleteEncuesta()
    suspend fun deleteConsultas()
    suspend fun deleteSeguimiento()
    suspend fun deleteEstados()
    suspend fun deleteBajas()
    suspend fun deleteAltas()
    suspend fun deleteDatosAltas()
    suspend fun deleteBajasSupervisor()
    suspend fun deleteRespuestas()
    suspend fun deleteIncidencias()

    ///     Room Query
    suspend fun queryConfiguracion(): TableConfiguracion?
    suspend fun queryClientes(): List<TableCliente>
    suspend fun queryVendedores(): List<TableVendedor>
    suspend fun queryDistritos(): List<TableDistrito>
    suspend fun queryNegocios(): List<TableNegocio>
    suspend fun queryRutas(): List<TableRuta>
    suspend fun queryEncuestas(): List<TableEncuesta>
    suspend fun queryBajaEspecifica(cliente: String): TableBaja?

    ///     Room Flow
    suspend fun listFlowConfiguracion(): Flow<List<TableConfiguracion>>
    suspend fun listFlowClientes(): Flow<List<FlowCliente>>
    suspend fun listFlowAltas(): Flow<List<TableAlta>>

    ///     Room Server
    suspend fun apiServerSeguimiento(sync: Boolean): List<TableSeguimiento>
    suspend fun apiServerAltas(sync: Boolean): List<TableAlta>
    suspend fun apiServerAltaDatos(sync: Boolean): List<TableAltaDatos>
    suspend fun apiServerBajas(sync: Boolean): List<TableBaja>
    suspend fun apiServerEstados(sync: Boolean): List<TableEstado>
    suspend fun apiServerRespuestas(sync: Boolean): List<TableRespuesta>
    suspend fun apiServerFotos(sync: Boolean): List<TableRespuesta>
}