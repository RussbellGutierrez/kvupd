package com.upd.kv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upd.kv.data.model.*
import com.upd.kv.data.model.QueryConstant.DEL_CLIENTES
import com.upd.kv.data.model.QueryConstant.DEL_DISTRITOS
import com.upd.kv.data.model.QueryConstant.DEL_EMPLEADOS
import com.upd.kv.data.model.QueryConstant.DEL_ENCUESTA
import com.upd.kv.data.model.QueryConstant.DEL_NEGOCIOS
import com.upd.kv.data.model.QueryConstant.DEL_SEGUIMIENTO
import com.upd.kv.data.model.QueryConstant.DEL_VISITA
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConf(conf: List<TConfiguracion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCli(cli: List<TClientes>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmp(emp: List<TEmpleados>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDist(dist: List<TDistrito>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNeg(neg: List<TNegocio>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnc(enc: List<TEncuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeguimiento(seg: TSeguimiento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisita(vis: TVisita)

    @Query(DEL_CLIENTES)
    suspend fun deleteClientes()

    @Query(DEL_EMPLEADOS)
    suspend fun deleteEmpleado()

    @Query(DEL_DISTRITOS)
    suspend fun deleteDistrito()

    @Query(DEL_NEGOCIOS)
    suspend fun deleteNegocio()

    @Query(DEL_ENCUESTA)
    suspend fun deleteEncuesta()

    @Query(DEL_SEGUIMIENTO)
    suspend fun deleteSeguimiento()

    @Query(DEL_VISITA)
    suspend fun deleteVisita()
}