package com.upd.kvupd.data.local.core

import androidx.room.Dao
import androidx.room.Query
import com.upd.kvupd.data.model.core.QueryCoreConstants.ALTADATO_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.ALTA_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.ALTA_DATO_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.ALTA_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.BAJA_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.BAJA_PROCESADO_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.BAJA_PROCESADO_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.BAJA_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.FOTO_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.FOTO_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_ALTADATOS
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_ALTAS
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_ALTAS_SPECIFIC
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_BAJAS
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_BAJAS_PROCESADAS
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_CONFIGURACION
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_FOTO_LISTA
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_LAST_GPS
import com.upd.kvupd.data.model.core.QueryCoreConstants.GET_RESPUESTAS
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_ALTA
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_ALTA_DATO
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_BAJA
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_BAJA_PROCESADO
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_FOTO
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_RESPUESTA
import com.upd.kvupd.data.model.core.QueryCoreConstants.LIMPIEZA_SEGUIMIENTO
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_ALTA
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_ALTA_DATO
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_BAJA
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_BAJA_PROCESADO
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_FOTO
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_RESPUESTA
import com.upd.kvupd.data.model.core.QueryCoreConstants.PENDIENTE_SEGUIMIENTO
import com.upd.kvupd.data.model.core.QueryCoreConstants.RESPUESTA_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.RESPUESTA_SERVER
import com.upd.kvupd.data.model.core.QueryCoreConstants.SEGUIMIENTO_COUNT
import com.upd.kvupd.data.model.core.QueryCoreConstants.SEGUIMIENTO_SERVER
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface CoreQuery {

    @Query(GET_CONFIGURACION)
    suspend fun getConfiguracion(): TableConfiguracion?

    @Query(GET_ALTAS_SPECIFIC)
    suspend fun getAltaSpecific(idaux: String, fecha: String): TableAlta?

    @Query(GET_ALTADATOS)
    suspend fun getAltaDatos(idaux: String, fecha: String): TableAltaDatos?

    @Query(GET_FOTO_LISTA)
    suspend fun getListFotoRutas(hoy: String): List<String>

    @Query(GET_CONFIGURACION)
    fun flowConfiguracion(): Flow<List<TableConfiguracion>>

    @Query(GET_ALTAS)
    fun flowAltas(): Flow<List<TableAlta>>

    @Query(GET_BAJAS)
    fun flowBajas(): Flow<List<TableBaja>>

    @Query(GET_LAST_GPS)
    fun flowLastSeguimiento(): Flow<TableSeguimiento?>

    @Query(GET_BAJAS_PROCESADAS)
    fun flowBajasProcesadas(): Flow<List<TableBajaProcesada>>

    @Query(GET_RESPUESTAS)
    fun flowRespuestas(): Flow<List<TableRespuesta>>

    @Query(SEGUIMIENTO_COUNT)
    suspend fun seguimientoCount(): Int

    @Query(ALTA_COUNT)
    suspend fun altaCount(): Int

    @Query(ALTADATO_COUNT)
    suspend fun altaDatoCount(): Int

    @Query(BAJA_COUNT)
    suspend fun bajaCount(): Int

    @Query(BAJA_PROCESADO_COUNT)
    suspend fun bajaProcesadaCount(): Int

    @Query(RESPUESTA_COUNT)
    suspend fun respuestaCount(): Int

    @Query(FOTO_COUNT)
    suspend fun fotoCount(): Int

    @Query(SEGUIMIENTO_SERVER)
    suspend fun serverSeguimiento(sync: Boolean): List<TableSeguimiento>

    @Query(ALTA_SERVER)
    suspend fun serverAltas(sync: Boolean): List<TableAlta>

    @Query(ALTA_DATO_SERVER)
    suspend fun serverAltaDatos(sync: Boolean): List<TableAltaDatos>

    @Query(BAJA_SERVER)
    suspend fun serverBajas(sync: Boolean): List<TableBaja>

    @Query(BAJA_PROCESADO_SERVER)
    suspend fun serverBajaProcesado(sync: Boolean): List<TableBajaProcesada>

    @Query(RESPUESTA_SERVER)
    suspend fun serverRespuestas(sync: Boolean): List<TableRespuesta>

    @Query(FOTO_SERVER)
    suspend fun serverFotos(sync: Boolean): List<TableFoto>

    @Query(PENDIENTE_SEGUIMIENTO)
    suspend fun hasSeguimientoPendiente(): Boolean

    @Query(PENDIENTE_ALTA)
    suspend fun hasAltasPendientes(): Boolean

    @Query(PENDIENTE_ALTA_DATO)
    suspend fun hasAltaDatosPendiente(): Boolean

    @Query(PENDIENTE_BAJA)
    suspend fun hasBajasPendientes(): Boolean

    @Query(PENDIENTE_BAJA_PROCESADO)
    suspend fun hasBajaProcesadaPendiente(): Boolean

    @Query(PENDIENTE_RESPUESTA)
    suspend fun hasRespuestasPendientes(): Boolean

    @Query(PENDIENTE_FOTO)
    suspend fun hasFotosPendientes(): Boolean

    @Query(LIMPIEZA_SEGUIMIENTO)
    suspend fun needSeguimientoLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_ALTA)
    suspend fun needAltasLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_ALTA_DATO)
    suspend fun needAltaDatosLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_BAJA)
    suspend fun needBajasLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_BAJA_PROCESADO)
    suspend fun needBajaProcesadaLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_RESPUESTA)
    suspend fun needRespuestasLimpiar(hoy: String): Boolean

    @Query(LIMPIEZA_FOTO)
    suspend fun needFotosLimpiar(hoy: String): Boolean
}