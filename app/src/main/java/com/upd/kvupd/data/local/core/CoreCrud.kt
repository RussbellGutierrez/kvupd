package com.upd.kvupd.data.local.core

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_ALTA
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_ALTADATOS
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_BAJA
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_BAJA_PROCESADA
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_CONFIGURACION
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_FOTO
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_RESPUESTA
import com.upd.kvupd.data.model.core.DeleteCoreConstants.DEL_SEGUIMIENTO
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento

@Dao
interface CoreCrud {

    @Transaction
    suspend fun clearServerUploadData(hoy: String) {
        deleteSeguimiento(hoy)
        deleteAlta(hoy)
        deleteAltaDatos(hoy)
        deleteBaja(hoy)
        deleteBajaProcesada(hoy)
        deleteRespuesta(hoy)
        deleteFoto(hoy)
    }

    @Transaction
    suspend fun replaceConfiguracion(data: List<TableConfiguracion>) {
        deleteConfiguracion()
        insertConfiguracion(data)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguracion(data: List<TableConfiguracion>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeguimiento(data: TableSeguimiento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaja(data: TableBaja)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaProcesada(data: TableBajaProcesada)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlta(data: TableAlta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAltaDatos(data: TableAltaDatos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(data: List<TableRespuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(data: TableFoto)

    @Update suspend fun updateSeguimiento(data: TableSeguimiento)
    @Update suspend fun updateAlta(data: TableAlta)
    @Update suspend fun updateAltaDatos(data: TableAltaDatos)
    @Update suspend fun updateBaja(data: TableBaja)
    @Update suspend fun updateBajaProcesada(data: TableBajaProcesada)
    @Update suspend fun updateRespuesta(data: TableRespuesta)
    @Update suspend fun updateFoto(data: TableFoto)

    @Query(DEL_CONFIGURACION)
    suspend fun deleteConfiguracion()

    @Query(DEL_SEGUIMIENTO)
    suspend fun deleteSeguimiento(hoy: String)

    @Query(DEL_BAJA)
    suspend fun deleteBaja(hoy: String)

    @Query(DEL_BAJA_PROCESADA)
    suspend fun deleteBajaProcesada(hoy: String)

    @Query(DEL_ALTA)
    suspend fun deleteAlta(hoy: String)

    @Query(DEL_ALTADATOS)
    suspend fun deleteAltaDatos(hoy: String)

    @Query(DEL_RESPUESTA)
    suspend fun deleteRespuesta(hoy: String)

    @Query(DEL_FOTO)
    suspend fun deleteFoto(hoy: String)
}