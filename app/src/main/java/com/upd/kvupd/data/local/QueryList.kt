package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Query
import com.upd.kvupd.data.model.Cabecera
import com.upd.kvupd.data.model.QueryConstant.ALTADATO_SERVER
import com.upd.kvupd.data.model.QueryConstant.ALTA_SERVER
import com.upd.kvupd.data.model.QueryConstant.BAJA_SERVER
import com.upd.kvupd.data.model.QueryConstant.ESTADO_SERVER
import com.upd.kvupd.data.model.QueryConstant.FOTO_SERVER
import com.upd.kvupd.data.model.QueryConstant.GET_CLIENTES
import com.upd.kvupd.data.model.QueryConstant.GET_CONFIGURACION
import com.upd.kvupd.data.model.QueryConstant.GET_DISTRITOS
import com.upd.kvupd.data.model.QueryConstant.GET_ENCUESTA
import com.upd.kvupd.data.model.QueryConstant.GET_NEGOCIOS
import com.upd.kvupd.data.model.QueryConstant.GET_RUTAS
import com.upd.kvupd.data.model.QueryConstant.GET_VENDEDORES
import com.upd.kvupd.data.model.QueryConstant.RESPUESTA_SERVER
import com.upd.kvupd.data.model.QueryConstant.SEGUIMIENTO_SERVER
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableEstado
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.TableVendedor

@Dao
interface QueryList {
    @Query(GET_CONFIGURACION)
    suspend fun getConfiguracion(): TableConfiguracion?

    @Query(GET_CLIENTES)
    suspend fun getClientes(): List<TableCliente>

    @Query(GET_VENDEDORES)
    suspend fun getVendedores(): List<TableVendedor>

    @Query(GET_DISTRITOS)
    suspend fun getDistritos(): List<TableDistrito>

    @Query(GET_NEGOCIOS)
    suspend fun getNegocios(): List<TableNegocio>

    @Query(GET_RUTAS)
    suspend fun getRutas(): List<TableRuta>

    @Query(GET_ENCUESTA)
    suspend fun getEncuestas(): List<TableEncuesta>

    ////  SERVER
    @Query(SEGUIMIENTO_SERVER)
    suspend fun seguimientoServer(estado: String): List<TableSeguimiento>

    @Query(ALTA_SERVER)
    suspend fun altaServer(estado: String): List<TableAlta>

    @Query(ALTADATO_SERVER)
    suspend fun datosAltaServer(estado: String): List<TableAltaDatos>

    @Query(BAJA_SERVER)
    suspend fun bajaServer(estado: String): List<TableBaja>

    @Query(ESTADO_SERVER)
    suspend fun estadosServer(estado: String): List<TableEstado>

    @Query(RESPUESTA_SERVER)
    suspend fun respuestaServer(estado: String): List<TableRespuesta>

    @Query(FOTO_SERVER)
    suspend fun fotoServer(estado: String): List<TableRespuesta>

    /////////////////////////////////////////////////          REVISAR
    /*@Query(GET_CONFIG)
    fun getObsConfig(): Flow<List<TConfiguracion>>

    @Query(GET_SESION)
    fun getObsSession(): Flow<TSesion?>

    @Query(GET_ROW_CLIENTES)
    fun getRowClientes(): Flow<List<RowCliente>>

    @Query(GET_LAST_LOCATION)
    fun getLastLocation(): Flow<List<TSeguimiento>?>

    @Query(GET_MARKERS)
    fun getMarkers(observacion: String): Flow<List<MarkerMap>>

    @Query(GET_ALTAS)
    fun getAltas(): Flow<List<TAlta>>

    @Query(GET_NEGOCIOS)
    fun getObsNegocios(): Flow<List<TNegocio>>

    @Query(GET_DISTRITOS)
    fun getObsDistritos(): Flow<List<TDistrito>>

    @Query(GET_BAJA)
    fun getBajas(): Flow<List<TBaja>>

    @Query(GET_ROW_BAJAS)
    fun getRowBajas(): Flow<List<RowBaja>>

    @Query(GET_RUTAS)
    fun getObsRutas(): Flow<List<TRutas>>

    @Query(GET_INCIDENCIA)
    fun getIncidencias(): Flow<List<TIncidencia>>



    @Query(GET_CONSULTA)
    suspend fun getConsulta(numero: String, nombre: String): List<TConsulta>

    @Query(GET_DATA_CLIENTE)
    suspend fun getDataCliente(cliente: String, observacion: String): List<DataCliente>

    @Query(GET_BAJA_SPECIFIC)
    suspend fun getBajaCliente(cliente: String): TBaja?

    @Query(GET_DATA_ALTA)
    suspend fun getDataAlta(alta: String): DataAlta

    @Query(GET_ALTADATOS)
    suspend fun getAltaDatoSpecific(alta: String): TADatos?

    @Query(GET_LAST_AUX)
    suspend fun getLastAux(): Int?

    @Query(GET_BAJA_SUPER)
    suspend fun getBajaSuper(codigo: String, fecha: String): TBajaSuper

    @Query(GET_SELECCION)
    suspend fun getSeleccionado(): TEncuestaSeleccionado?

    @Query(GET_RESPUESTA_CLIENTE)
    suspend fun getRespuesta(cliente: String): RespuestaCliente?

    @Query(GET_RESPUESTA_HISTORICO)
    suspend fun getRespuestaH(cliente: String): RespuestaHistorico

    */
}