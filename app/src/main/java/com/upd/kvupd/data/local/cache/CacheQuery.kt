package com.upd.kvupd.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_BAJA_SUPERVISOR
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_CLIENTES
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_DISTRITOS
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_ENCUESTA
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_HEADER_ENCUESTAS
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_NEGOCIOS
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_RUTAS
import com.upd.kvupd.data.model.cache.QueryCacheConstants.GET_VENDEDORES
import com.upd.kvupd.data.model.cache.QueryCacheConstants.UPDATE_CLEAR_ENCUESTA
import com.upd.kvupd.data.model.cache.QueryCacheConstants.UPDATE_SET_SELECCION
import com.upd.kvupd.data.model.cache.TableBajaSupervisor
import com.upd.kvupd.data.model.cache.TableCliente
import com.upd.kvupd.data.model.cache.TableDistrito
import com.upd.kvupd.data.model.cache.TableEncuesta
import com.upd.kvupd.data.model.cache.TableNegocio
import com.upd.kvupd.data.model.cache.TableRuta
import com.upd.kvupd.data.model.cache.TableVendedor
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheQuery {

    @Query(GET_CLIENTES)
    suspend fun getClientes(): List<TableCliente>

    @Query(GET_DISTRITOS)
    suspend fun getDistritos(): List<TableDistrito>

    @Query(GET_NEGOCIOS)
    suspend fun getNegocios(): List<TableNegocio>

    @Query(GET_RUTAS)
    suspend fun getRutas(): List<TableRuta>

    @Query(GET_HEADER_ENCUESTAS)
    suspend fun getHeadersEncuesta(): List<FlowHeaderEncuestas>

    @Query(GET_RUTAS)
    fun flowRutas(): Flow<List<TableRuta>>

    @Query(GET_NEGOCIOS)
    fun flowNegocios(): Flow<List<TableNegocio>>

    @Query(GET_DISTRITOS)
    fun flowDistritos(): Flow<List<TableDistrito>>

    @Query(GET_CLIENTES)
    fun flowClientes(): Flow<List<TableCliente>>

    @Query(GET_VENDEDORES)
    fun flowVendedores(): Flow<List<TableVendedor>>

    @Query(GET_BAJA_SUPERVISOR)
    fun flowBajasSupervisor(): Flow<List<TableBajaSupervisor>>

    @Query(GET_ENCUESTA)
    fun flowPreguntaEncuestas(): Flow<List<TableEncuesta>>

    @Query(GET_HEADER_ENCUESTAS)
    fun flowHeaderEncuestas(): Flow<List<FlowHeaderEncuestas>>

    @Transaction
    suspend fun reselectEncuesta(id: String) {
        cleanSeleccionEncuestas()
        setSeleccionEncuesta(id)
    }

    @Query(UPDATE_CLEAR_ENCUESTA)
    suspend fun cleanSeleccionEncuestas()

    @Query(UPDATE_SET_SELECCION)
    suspend fun setSeleccionEncuesta(id: String)
}