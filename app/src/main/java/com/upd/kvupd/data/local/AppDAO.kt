package com.upd.kvupd.data.local

import androidx.room.*
import com.upd.kvupd.data.model.*
import com.upd.kvupd.data.model.QueryConstant.DEL_AFOTO
import com.upd.kvupd.data.model.QueryConstant.DEL_ALTA
import com.upd.kvupd.data.model.QueryConstant.DEL_ALTADATOS
import com.upd.kvupd.data.model.QueryConstant.DEL_BAJA
import com.upd.kvupd.data.model.QueryConstant.DEL_BAJASUPER
import com.upd.kvupd.data.model.QueryConstant.DEL_CLIENTES
import com.upd.kvupd.data.model.QueryConstant.DEL_CONFIG
import com.upd.kvupd.data.model.QueryConstant.DEL_DISTRITOS
import com.upd.kvupd.data.model.QueryConstant.DEL_EMPLEADOS
import com.upd.kvupd.data.model.QueryConstant.DEL_ENCUESTA
import com.upd.kvupd.data.model.QueryConstant.DEL_ESTADO
import com.upd.kvupd.data.model.QueryConstant.DEL_ESTADOBAJA
import com.upd.kvupd.data.model.QueryConstant.DEL_INCIDENCIA
import com.upd.kvupd.data.model.QueryConstant.DEL_NEGOCIOS
import com.upd.kvupd.data.model.QueryConstant.DEL_RESPUESTA
import com.upd.kvupd.data.model.QueryConstant.DEL_RUTAS
import com.upd.kvupd.data.model.QueryConstant.DEL_SEGUIMIENTO
import com.upd.kvupd.data.model.QueryConstant.DEL_SELECCION
import com.upd.kvupd.data.model.QueryConstant.DEL_VISITA

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesion(sesion: TSesion)

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
    suspend fun insertRut(rut: List<TRutas>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnc(enc: List<TEncuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeguimiento(seg: TSeguimiento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisita(vis: TVisita)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstado(est: TEstado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaja(baja: TBaja)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlta(alta: TAlta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAltaDatos(da: TADatos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaSupervisor(baja: List<TBajaSuper>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstadoBaja(estado: TBEstado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeleccionado(selec: TEncuestaSeleccionado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(rsp: List<TRespuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuestaIndividual(rsp: TRespuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(rsp: TRespuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidencia(rsp: TIncidencia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAFoto(rsp: TAFoto)

    @Update(entity = TAlta::class)
    suspend fun updateLocationAlta(upd: LocationAlta)

    @Update(entity = TAlta::class)
    suspend fun updateMiniAlta(upd: MiniUpdAlta)

    @Update(entity = TADatos::class)
    suspend fun updateAltaDatos(upd: TADatos)

    @Update(entity = TBaja::class)
    suspend fun updateMiniBaja(upd: MiniUpdBaja)

    @Query(DEL_CONFIG)
    suspend fun deleteConfig()

    @Query(DEL_CLIENTES)
    suspend fun deleteClientes()

    @Query(DEL_EMPLEADOS)
    suspend fun deleteEmpleado()

    @Query(DEL_DISTRITOS)
    suspend fun deleteDistrito()

    @Query(DEL_NEGOCIOS)
    suspend fun deleteNegocio()

    @Query(DEL_RUTAS)
    suspend fun deleteRutas()

    @Query(DEL_ENCUESTA)
    suspend fun deleteEncuesta()

    @Query(DEL_SEGUIMIENTO)
    suspend fun deleteSeguimiento()

    @Query(DEL_VISITA)
    suspend fun deleteVisita()

    @Query(DEL_ESTADO)
    suspend fun deleteEstado()

    @Query(DEL_BAJA)
    suspend fun deleteBaja()

    @Query(DEL_ALTA)
    suspend fun deleteAlta()

    @Query(DEL_ALTADATOS)
    suspend fun deleteAltaDatos()

    @Query(DEL_BAJASUPER)
    suspend fun deleteBajaSuper()

    @Query(DEL_ESTADOBAJA)
    suspend fun deleteEstadoBaja()

    @Query(DEL_SELECCION)
    suspend fun deleteSeleccionado()

    @Query(DEL_RESPUESTA)
    suspend fun deleteRespuesta()

    @Query(DEL_INCIDENCIA)
    suspend fun deleteIncidencia()

    @Query(DEL_AFOTO)
    suspend fun deleteAFoto()
}