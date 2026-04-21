package com.upd.kvupd.data.local.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_BAJA_SUPERVISOR
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_CLIENTES
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_DISTRITOS
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_ENCUESTA
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_NEGOCIOS
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_RUTAS
import com.upd.kvupd.data.model.cache.DeleteCacheConstants.DEL_VENDEDOR
import com.upd.kvupd.data.model.cache.TableBajaSupervisor
import com.upd.kvupd.data.model.cache.TableCliente
import com.upd.kvupd.data.model.cache.TableDistrito
import com.upd.kvupd.data.model.cache.TableEncuesta
import com.upd.kvupd.data.model.cache.TableNegocio
import com.upd.kvupd.data.model.cache.TableRuta
import com.upd.kvupd.data.model.cache.TableVendedor

@Dao
interface CacheCrud {

    @Transaction
    suspend fun clearSessionData() {
        deleteBajaSupervisor()
    }

    @Transaction
    suspend fun replaceClientes(data: List<TableCliente>) {
        deleteClientes()
        insertClientes(data)
    }

    @Transaction
    suspend fun replaceVendedores(data: List<TableVendedor>) {
        deleteVendedores()
        insertVendedores(data)
    }

    @Transaction
    suspend fun replaceDistritos(data: List<TableDistrito>) {
        deleteDistritos()
        insertDistritos(data)
    }

    @Transaction
    suspend fun replaceNegocios(data: List<TableNegocio>) {
        deleteNegocios()
        insertNegocios(data)
    }

    @Transaction
    suspend fun replaceRutas(data: List<TableRuta>) {
        deleteRutas()
        insertRutas(data)
    }

    @Transaction
    suspend fun replaceEncuesta(data: List<TableEncuesta>) {
        deleteEncuesta()
        insertEncuestas(data)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientes(data: List<TableCliente>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendedores(data: List<TableVendedor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistritos(data: List<TableDistrito>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegocios(data: List<TableNegocio>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutas(data: List<TableRuta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuestas(data: List<TableEncuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaSupervisor(data: List<TableBajaSupervisor>)

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
}