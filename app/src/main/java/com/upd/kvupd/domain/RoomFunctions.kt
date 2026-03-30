package com.upd.kvupd.domain

import com.upd.kvupd.data.model.BajaSupervisor
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.Configuracion
import com.upd.kvupd.data.model.Distrito
import com.upd.kvupd.data.model.Encuesta
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.Negocio
import com.upd.kvupd.data.model.Ruta
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.TableVendedor
import com.upd.kvupd.data.model.Vendedor
import kotlinx.coroutines.flow.Flow

interface RoomFunctions {
    ///     Room Transactions
    suspend fun replaceConfiguracion(item: List<Configuracion>)
    suspend fun replaceClientes(item: List<Cliente>)
    suspend fun replaceVendedores(item: List<Vendedor>)
    suspend fun replaceDistritos(item: List<Distrito>)
    suspend fun replaceNegocios(item: List<Negocio>)
    suspend fun replaceRutas(item: List<Ruta>)
    suspend fun replaceEncuesta(item: List<Encuesta>)
    suspend fun reselectEncuesta(id: String)

    ///     Room Api
    suspend fun apiSaveConfiguracion(item: List<Configuracion>)
    suspend fun apiSaveClientes(item: List<Cliente>)
    suspend fun apiSaveVendedores(item: List<Vendedor>)
    suspend fun apiSaveDistritos(item: List<Distrito>)
    suspend fun apiSaveNegocios(item: List<Negocio>)
    suspend fun apiSaveRutas(item: List<Ruta>)
    suspend fun apiSaveEncuesta(item: List<Encuesta>)
    suspend fun apiSaveBajaSupervisor(item: List<BajaSupervisor>)

    ///     Room Insert
    suspend fun saveSeguimiento(item: TableSeguimiento)
    suspend fun saveBaja(item: TableBaja)
    suspend fun saveBajaProcesada(item: TableBajaProcesada)
    suspend fun saveAlta(item: TableAlta)
    suspend fun saveDatosAlta(item: TableAltaDatos)
    suspend fun saveRespuestas(item: List<TableRespuesta>)
    suspend fun saveFoto(item: TableFoto)

    ///     Room Update
    suspend fun updateSeguimiento(actual: TableSeguimiento)
    suspend fun updateAlta(actual: TableAlta)
    suspend fun updateDatosAlta(actual: TableAltaDatos)
    suspend fun updateBaja(actual: TableBaja)
    suspend fun updateBajaProcesada(actual: TableBajaProcesada)
    suspend fun updateRespuesta(actual: TableRespuesta)
    suspend fun updateFoto(actual: TableFoto)
    suspend fun updateEncuestaSeleccion(id: String)

    ///     Room Delete
    suspend fun deleteConfiguracion()
    suspend fun deleteClientes()
    suspend fun deleteVendedores()
    suspend fun deleteDistritos()
    suspend fun deleteNegocios()
    suspend fun deleteRutas()
    suspend fun deleteEncuesta()
    suspend fun deleteSeguimiento()
    suspend fun deleteBajas()
    suspend fun deleteBajasProcesadas()
    suspend fun deleteAltas()
    suspend fun deleteDatosAltas()
    suspend fun deleteBajasSupervisor()
    suspend fun deleteRespuestas()
    suspend fun deleteFoto()

    ///     Room Query
    suspend fun queryConfiguracion(): TableConfiguracion?
    suspend fun queryClientes(): List<TableCliente>
    suspend fun queryDistritos(): List<TableDistrito>
    suspend fun queryNegocios(): List<TableNegocio>
    suspend fun queryRutas(): List<TableRuta>
    suspend fun queryAltaSpecific(idaux: String, fecha: String): TableAlta?
    suspend fun queryAltaDatos(idaux: String, fecha: String): TableAltaDatos?
    suspend fun queryCabeceraEncuesta(): List<FlowHeaderEncuestas>

    ///     Room Flow
    fun listFlowConfiguracion(): Flow<List<TableConfiguracion>>
    fun listFlowClientes(): Flow<List<FlowCliente>>
    fun listFlowBajaSupervisor(): Flow<List<FlowBajaSupervisor>>
    fun listFlowAltas(): Flow<List<TableAlta>>
    fun listFlowBajas(): Flow<List<TableBaja>>
    fun listFlowRutas(): Flow<List<TableRuta>>
    fun listFlowNegocios(): Flow<List<TableNegocio>>
    fun listFlowDistritos(): Flow<List<TableDistrito>>
    fun listFlowVendedores(): Flow<List<TableVendedor>>
    fun listFlowLastGPS(): Flow<TableSeguimiento?>
    fun listFlowPreguntas(): Flow<List<TableEncuesta>>
    fun listFlowCabeceraEncuesta(): Flow<List<FlowHeaderEncuestas>>
    fun listFlowClientesPendientes(encuestaId: String): Flow<List<TableCliente>>

    ///     Room Server
    suspend fun apiServerSeguimiento(sync: Boolean): List<TableSeguimiento>
    suspend fun apiServerAltas(sync: Boolean): List<TableAlta>
    suspend fun apiServerAltaDatos(sync: Boolean): List<TableAltaDatos>
    suspend fun apiServerBajas(sync: Boolean): List<TableBaja>
    suspend fun apiServerBajasProcesadas(sync: Boolean): List<TableBajaProcesada>
    suspend fun apiServerRespuestas(sync: Boolean): List<TableRespuesta>
    suspend fun apiServerFotos(sync: Boolean): List<TableFoto>
}