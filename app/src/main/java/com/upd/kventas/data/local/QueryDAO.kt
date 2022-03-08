package com.upd.kventas.data.local

import androidx.room.Dao
import androidx.room.Query
import com.upd.kventas.data.model.*
import com.upd.kventas.data.model.QueryConstant.GET_ALTADATOS
import com.upd.kventas.data.model.QueryConstant.GET_ALTAS
import com.upd.kventas.data.model.QueryConstant.GET_BAJA
import com.upd.kventas.data.model.QueryConstant.GET_BAJA_SPECIFIC
import com.upd.kventas.data.model.QueryConstant.GET_BAJA_SUPER
import com.upd.kventas.data.model.QueryConstant.GET_CLIENTES
import com.upd.kventas.data.model.QueryConstant.GET_CONFIG
import com.upd.kventas.data.model.QueryConstant.GET_DATA_ALTA
import com.upd.kventas.data.model.QueryConstant.GET_DATA_CLIENTE
import com.upd.kventas.data.model.QueryConstant.GET_DISTRITOS
import com.upd.kventas.data.model.QueryConstant.GET_EMPLEADOS
import com.upd.kventas.data.model.QueryConstant.GET_LAST_ALTA
import com.upd.kventas.data.model.QueryConstant.GET_LAST_LOCATION
import com.upd.kventas.data.model.QueryConstant.GET_MARKERS
import com.upd.kventas.data.model.QueryConstant.GET_NEGOCIOS
import com.upd.kventas.data.model.QueryConstant.GET_ROW_BAJAS
import com.upd.kventas.data.model.QueryConstant.GET_ROW_CLIENTES
import com.upd.kventas.data.model.QueryConstant.GET_VISITA
import kotlinx.coroutines.flow.Flow

@Dao
interface QueryDAO {

    @Query(GET_CONFIG)
    fun getObsConfig(): Flow<List<TConfiguracion>>

    @Query(GET_ROW_CLIENTES)
    fun getRowClientes(): Flow<List<RowCliente>>

    @Query(GET_LAST_LOCATION)
    fun getLastLocation(): Flow<List<TSeguimiento>>

    @Query(GET_MARKERS)
    fun getMarkers(): Flow<List<MarkerMap>>

    @Query(GET_ALTAS)
    fun getAltas(): Flow<List<TAlta>>

    @Query(GET_NEGOCIOS)
    fun getObsNegocios(): Flow<List<Combo>>

    @Query(GET_DISTRITOS)
    fun getObsDistritos(): Flow<List<Combo>>

    @Query(GET_BAJA)
    fun getBajas(): Flow<List<TBaja>>

    @Query(GET_ROW_BAJAS)
    fun getRowBajas(): Flow<List<RowBaja>>

    @Query(GET_CONFIG)
    suspend fun getConfig(): List<TConfiguracion>

    @Query(GET_CLIENTES)
    suspend fun getClientes(): List<TClientes>

    @Query(GET_EMPLEADOS)
    suspend fun getEmpleados(): List<TEmpleados>

    @Query(GET_DISTRITOS)
    suspend fun getDistrito(): List<TDistrito>

    @Query(GET_NEGOCIOS)
    suspend fun getNegocio(): List<TNegocio>

    @Query(GET_DATA_CLIENTE)
    suspend fun getDataCliente(cliente: String): List<DataCliente>

    @Query(GET_BAJA_SPECIFIC)
    suspend fun getBajaCliente(cliente: String): TBaja?

    @Query(GET_DATA_ALTA)
    suspend fun getDataAlta(alta: String): DataCliente

    @Query(GET_ALTADATOS)
    suspend fun getAltaDatoSpecific(alta: String): TADatos?

    @Query(GET_LAST_ALTA)
    suspend fun getLastAlta(): TAlta?

    @Query(GET_BAJA_SUPER)
    suspend fun getBajaSuper(codigo: String, fecha: String): TBajaSuper

    /*  Dao send data to server    */
    @Query(GET_VISITA)
    suspend fun visitaServer(): List<TVisita>
}