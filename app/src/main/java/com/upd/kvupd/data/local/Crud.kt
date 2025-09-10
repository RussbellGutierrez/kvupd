package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.upd.kvupd.data.model.CrudConstant.DEL_ALTA
import com.upd.kvupd.data.model.CrudConstant.DEL_ALTADATOS
import com.upd.kvupd.data.model.CrudConstant.DEL_BAJA
import com.upd.kvupd.data.model.CrudConstant.DEL_BAJA_SUPERVISOR
import com.upd.kvupd.data.model.CrudConstant.DEL_CLIENTES
import com.upd.kvupd.data.model.CrudConstant.DEL_CONFIGURACION
import com.upd.kvupd.data.model.CrudConstant.DEL_CONSULTA
import com.upd.kvupd.data.model.CrudConstant.DEL_DISTRITOS
import com.upd.kvupd.data.model.CrudConstant.DEL_ENCUESTA
import com.upd.kvupd.data.model.CrudConstant.DEL_ESTADO
import com.upd.kvupd.data.model.CrudConstant.DEL_INCIDENCIA
import com.upd.kvupd.data.model.CrudConstant.DEL_NEGOCIOS
import com.upd.kvupd.data.model.CrudConstant.DEL_RESPUESTA
import com.upd.kvupd.data.model.CrudConstant.DEL_RUTAS
import com.upd.kvupd.data.model.CrudConstant.DEL_SEGUIMIENTO
import com.upd.kvupd.data.model.CrudConstant.DEL_VENDEDOR
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaSupervisor
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableConsulta
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableEstado
import com.upd.kvupd.data.model.TableIncidencia
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.TableVendedor

@Dao
interface Crud {
    ////  INSERTS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguracion(conf: List<TableConfiguracion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientes(cli: List<TableCliente>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendedores(emp: List<TableVendedor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistritos(dist: List<TableDistrito>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegocios(neg: List<TableNegocio>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutas(rut: List<TableRuta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuestas(enc: List<TableEncuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(cons: List<TableConsulta>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeguimiento(seg: TableSeguimiento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstado(est: TableEstado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaja(baja: TableBaja)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlta(alta: TableAlta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAltaDatos(da: TableAltaDatos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaSupervisor(baja: List<TableBajaSupervisor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(rsp: List<TableRespuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidencia(rsp: TableIncidencia)

    //// UPDATES
    @Update(entity = TableSeguimiento::class)
    suspend fun updateSeguimiento(upd: TableSeguimiento)

    @Update(entity = TableAlta::class)
    suspend fun updateAlta(upd: TableAlta)

    @Update(entity = TableBaja::class)
    suspend fun updateBaja(upd: TableBaja)

    @Update(entity = TableRespuesta::class)
    suspend fun updateRespuesta(rsp: TableRespuesta)

    @Update(entity = TableAltaDatos::class)
    suspend fun updateAltaDatos(upd: TableAltaDatos)

    //// DELETES
    @Query(DEL_CONFIGURACION)
    suspend fun deleteConfiguracion()

    @Query(DEL_CLIENTES)
    suspend fun deleteClientes()

    @Query(DEL_VENDEDOR)
    suspend fun deleteVendedores()

    @Query(DEL_DISTRITOS)
    suspend fun deleteDistritos()

    @Query(DEL_NEGOCIOS)
    suspend fun deleteNegocios()

    @Query(DEL_RUTAS)
    suspend fun deleteRutas()

    @Query(DEL_ENCUESTA)
    suspend fun deleteEncuesta()

    @Query(DEL_CONSULTA)
    suspend fun deleteConsulta()

    @Query(DEL_SEGUIMIENTO)
    suspend fun deleteSeguimiento()

    @Query(DEL_ESTADO)
    suspend fun deleteEstado()

    @Query(DEL_BAJA)
    suspend fun deleteBaja()

    @Query(DEL_ALTA)
    suspend fun deleteAlta()

    @Query(DEL_ALTADATOS)
    suspend fun deleteAltaDatos()

    @Query(DEL_BAJA_SUPERVISOR)
    suspend fun deleteBajaSupervisor()

    @Query(DEL_RESPUESTA)
    suspend fun deleteRespuesta()

    @Query(DEL_INCIDENCIA)
    suspend fun deleteIncidencia()
}