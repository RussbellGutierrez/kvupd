package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.upd.kvupd.data.model.DeleteConstant.DEL_ALTA
import com.upd.kvupd.data.model.DeleteConstant.DEL_ALTADATOS
import com.upd.kvupd.data.model.DeleteConstant.DEL_BAJA
import com.upd.kvupd.data.model.DeleteConstant.DEL_BAJA_PROCESADA
import com.upd.kvupd.data.model.DeleteConstant.DEL_BAJA_SUPERVISOR
import com.upd.kvupd.data.model.DeleteConstant.DEL_CLIENTES
import com.upd.kvupd.data.model.DeleteConstant.DEL_CONFIGURACION
import com.upd.kvupd.data.model.DeleteConstant.DEL_DISTRITOS
import com.upd.kvupd.data.model.DeleteConstant.DEL_ENCUESTA
import com.upd.kvupd.data.model.DeleteConstant.DEL_FOTO
import com.upd.kvupd.data.model.DeleteConstant.DEL_NEGOCIOS
import com.upd.kvupd.data.model.DeleteConstant.DEL_RESPUESTA
import com.upd.kvupd.data.model.DeleteConstant.DEL_RUTAS
import com.upd.kvupd.data.model.DeleteConstant.DEL_SEGUIMIENTO
import com.upd.kvupd.data.model.DeleteConstant.DEL_VENDEDOR
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableBajaSupervisor
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

@Dao
interface Crud {
    ////  TRANSACTIONS
    @Transaction
    suspend fun clearSessionData() {
        ///     Agregar los datos que se descargan bajo demanda
        deleteBajaSupervisor()
    }

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
    suspend fun replaceConfiguracion(configuracion: List<TableConfiguracion>) {
        deleteConfiguracion()
        insertConfiguracion(configuracion)
    }

    @Transaction
    suspend fun replaceClientes(cliente: List<TableCliente>) {
        deleteClientes()
        insertClientes(cliente)
    }

    @Transaction
    suspend fun replaceVendedores(empleado: List<TableVendedor>) {
        deleteVendedores()
        insertVendedores(empleado)
    }

    @Transaction
    suspend fun replaceDistritos(distrito: List<TableDistrito>) {
        deleteDistritos()
        insertDistritos(distrito)
    }

    @Transaction
    suspend fun replaceNegocios(negocio: List<TableNegocio>) {
        deleteNegocios()
        insertNegocios(negocio)
    }

    @Transaction
    suspend fun replaceRutas(ruta: List<TableRuta>) {
        deleteRutas()
        insertRutas(ruta)
    }

    @Transaction
    suspend fun replaceEncuesta(encuesta: List<TableEncuesta>) {
        deleteEncuesta()
        insertEncuestas(encuesta)
    }

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeguimiento(seg: TableSeguimiento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaja(baja: TableBaja)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaProcesada(est: TableBajaProcesada)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlta(alta: TableAlta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAltaDatos(da: TableAltaDatos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaSupervisor(baja: List<TableBajaSupervisor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(rsp: List<TableRespuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(foto: TableFoto)

    //// UPDATES
    @Update(entity = TableSeguimiento::class)
    suspend fun updateSeguimiento(upd: TableSeguimiento)

    @Update(entity = TableAlta::class)
    suspend fun updateAlta(upd: TableAlta)

    @Update(entity = TableBaja::class)
    suspend fun updateBaja(upd: TableBaja)

    @Update(entity = TableBajaProcesada::class)
    suspend fun updateBajaProcesada(upd: TableBajaProcesada)

    @Update(entity = TableRespuesta::class)
    suspend fun updateRespuesta(rsp: TableRespuesta)

    @Update(entity = TableFoto::class)
    suspend fun updateFoto(foto: TableFoto)

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

    @Query(DEL_BAJA_SUPERVISOR)
    suspend fun deleteBajaSupervisor()

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