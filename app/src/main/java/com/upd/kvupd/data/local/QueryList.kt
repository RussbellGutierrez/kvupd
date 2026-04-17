package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.QueryConstant.ALTADATO_COUNT
import com.upd.kvupd.data.model.QueryConstant.ALTA_COUNT
import com.upd.kvupd.data.model.QueryConstant.ALTA_DATO_SERVER
import com.upd.kvupd.data.model.QueryConstant.ALTA_SERVER
import com.upd.kvupd.data.model.QueryConstant.BAJA_COUNT
import com.upd.kvupd.data.model.QueryConstant.BAJA_PROCESADO_COUNT
import com.upd.kvupd.data.model.QueryConstant.BAJA_PROCESADO_SERVER
import com.upd.kvupd.data.model.QueryConstant.BAJA_SERVER
import com.upd.kvupd.data.model.QueryConstant.FOTO_COUNT
import com.upd.kvupd.data.model.QueryConstant.FOTO_SERVER
import com.upd.kvupd.data.model.QueryConstant.GET_ALTADATOS
import com.upd.kvupd.data.model.QueryConstant.GET_ALTAS
import com.upd.kvupd.data.model.QueryConstant.GET_ALTAS_SPECIFIC
import com.upd.kvupd.data.model.QueryConstant.GET_BAJAS
import com.upd.kvupd.data.model.QueryConstant.GET_CLIENTES
import com.upd.kvupd.data.model.QueryConstant.GET_CLIENTES_EXCLUIDOS
import com.upd.kvupd.data.model.QueryConstant.GET_CONFIGURACION
import com.upd.kvupd.data.model.QueryConstant.GET_DISTRITOS
import com.upd.kvupd.data.model.QueryConstant.GET_ENCUESTA
import com.upd.kvupd.data.model.QueryConstant.GET_FOTO_LISTA
import com.upd.kvupd.data.model.QueryConstant.GET_HEADER_ENCUESTAS
import com.upd.kvupd.data.model.QueryConstant.GET_LAST_GPS
import com.upd.kvupd.data.model.QueryConstant.GET_NEGOCIOS
import com.upd.kvupd.data.model.QueryConstant.GET_RECYCLER_BAJASUPER
import com.upd.kvupd.data.model.QueryConstant.GET_RECYCLER_CLIENTE
import com.upd.kvupd.data.model.QueryConstant.GET_RUTAS
import com.upd.kvupd.data.model.QueryConstant.GET_VENDEDORES
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_ALTA
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_ALTA_DATO
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_BAJA
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_BAJA_PROCESADO
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_FOTO
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_RESPUESTA
import com.upd.kvupd.data.model.QueryConstant.LIMPIEZA_SEGUIMIENTO
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_ALTA
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_ALTA_DATO
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_BAJA
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_BAJA_PROCESADO
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_FOTO
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_RESPUESTA
import com.upd.kvupd.data.model.QueryConstant.PENDIENTE_SEGUIMIENTO
import com.upd.kvupd.data.model.QueryConstant.RESPUESTA_COUNT
import com.upd.kvupd.data.model.QueryConstant.RESPUESTA_SERVER
import com.upd.kvupd.data.model.QueryConstant.SEGUIMIENTO_COUNT
import com.upd.kvupd.data.model.QueryConstant.SEGUIMIENTO_SERVER
import com.upd.kvupd.data.model.QueryConstant.UPDATE_CLEAR_ENCUESTA
import com.upd.kvupd.data.model.QueryConstant.UPDATE_SET_SELECCION
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
import kotlinx.coroutines.flow.Flow

@Dao
interface QueryList {
    @Query(GET_CONFIGURACION)
    suspend fun getConfiguracion(): TableConfiguracion?

    @Query(GET_CLIENTES)
    suspend fun getClientes(): List<TableCliente>

    @Query(GET_DISTRITOS)
    suspend fun getDistritos(): List<TableDistrito>

    @Query(GET_NEGOCIOS)
    suspend fun getNegocios(): List<TableNegocio>

    @Query(GET_RUTAS)
    suspend fun getRutas(): List<TableRuta>

    @Query(GET_ALTAS_SPECIFIC)
    suspend fun getAltaSpecific(idaux: String, fecha: String): TableAlta?

    @Query(GET_ALTADATOS)
    suspend fun getAltaDatos(idaux: String, fecha: String): TableAltaDatos?

    @Query(GET_HEADER_ENCUESTAS)
    suspend fun getHeadersEncuesta(): List<FlowHeaderEncuestas>

    @Query(GET_FOTO_LISTA)
    suspend fun getListFotoRutas(hoy: String): List<String>

    ////  FLOW
    @Query(GET_CONFIGURACION)
    fun flowConfiguracion(): Flow<List<TableConfiguracion>>

    @Query(GET_RECYCLER_CLIENTE)
    fun flowClientes(): Flow<List<FlowCliente>>

    @Query(GET_RECYCLER_BAJASUPER)
    fun flowBajasSupervisor(): Flow<List<FlowBajaSupervisor>>

    @Query(GET_ALTAS)
    fun flowAltas(): Flow<List<TableAlta>>

    @Query(GET_BAJAS)
    fun flowBajas(): Flow<List<TableBaja>>

    @Query(GET_RUTAS)
    fun flowRutas(): Flow<List<TableRuta>>

    @Query(GET_NEGOCIOS)
    fun flowNegocios(): Flow<List<TableNegocio>>

    @Query(GET_DISTRITOS)
    fun flowDistritos(): Flow<List<TableDistrito>>

    @Query(GET_VENDEDORES)
    fun flowVendedores(): Flow<List<TableVendedor>>

    @Query(GET_LAST_GPS)
    fun flowLastSeguimiento(): Flow<TableSeguimiento?>

    @Query(GET_ENCUESTA)
    fun flowPreguntaEncuestas(): Flow<List<TableEncuesta>>

    @Query(GET_HEADER_ENCUESTAS)
    fun flowHeaderEncuestas(): Flow<List<FlowHeaderEncuestas>>

    @Query(GET_CLIENTES_EXCLUIDOS)
    fun flowClientesExcluidos(encuestaId: String): Flow<List<TableCliente>>

    ////  UPDATE MANUAL
    @Transaction
    suspend fun reselectEncuesta(id: String) {
        cleanSeleccionEncuestas()
        setSeleccionEncuesta(id)
    }

    @Query(UPDATE_CLEAR_ENCUESTA)
    suspend fun cleanSeleccionEncuestas()

    @Query(UPDATE_SET_SELECCION)
    suspend fun setSeleccionEncuesta(id: String)

    ////  TOTAL EN ROOM
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

    ////  SERVER
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

    ////  PENDIENTES DE ENVIO
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

    ////  LIMPIEZA DATOS
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