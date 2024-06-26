package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.upd.kvupd.data.model.LocationAlta
import com.upd.kvupd.data.model.MiniUpdAlta
import com.upd.kvupd.data.model.MiniUpdBaja
import com.upd.kvupd.data.model.QueryConstant.DEL_AAUX
import com.upd.kvupd.data.model.QueryConstant.DEL_AFOTO
import com.upd.kvupd.data.model.QueryConstant.DEL_ALTA
import com.upd.kvupd.data.model.QueryConstant.DEL_ALTADATOS
import com.upd.kvupd.data.model.QueryConstant.DEL_BAJA
import com.upd.kvupd.data.model.QueryConstant.DEL_BAJASUPER
import com.upd.kvupd.data.model.QueryConstant.DEL_CLIENTES
import com.upd.kvupd.data.model.QueryConstant.DEL_CONFIG
import com.upd.kvupd.data.model.QueryConstant.DEL_CONSULTA
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
import com.upd.kvupd.data.model.TAAux
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.data.model.TClientes
import com.upd.kvupd.data.model.TConfiguracion
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.data.model.TDistrito
import com.upd.kvupd.data.model.TEmpleados
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.data.model.TEstado
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.data.model.TNegocio
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TSesion
import com.upd.kvupd.data.model.TVisita

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
    suspend fun insertCons(cons: List<TConsulta>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    suspend fun insertAAux(aux: TAAux)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBajaSupervisor(baja: List<TBajaSuper>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstadoBaja(estado: TBEstado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeleccionado(selec: TEncuestaSeleccionado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(rsp: List<TRespuesta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidencia(rsp: TIncidencia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAFoto(rsp: TAFoto)

    @Update(entity = TSeguimiento::class)
    suspend fun updateSeguimiento(upd: TSeguimiento)

    @Update(entity = TVisita::class)
    suspend fun updateVisita(upd: TVisita)

    @Update(entity = TAlta::class)
    suspend fun updateAlta(upd: TAlta)

    @Update(entity = TBaja::class)
    suspend fun updateBaja(upd: TBaja)

    @Update(entity = TBEstado::class)
    suspend fun updateBajaEstado(upd: TBEstado)

    @Update(entity = TRespuesta::class)
    suspend fun updateRespuesta(rsp: TRespuesta)

    @Update(entity = TADatos::class)
    suspend fun updateAltaDatos(upd: TADatos)

    @Update(entity = TAFoto::class)
    suspend fun updateAltaFoto(upd: TAFoto)

    @Update(entity = TAlta::class)
    suspend fun updateLocationAlta(upd: LocationAlta)

    @Update(entity = TAlta::class)
    suspend fun updateMiniAlta(upd: MiniUpdAlta)

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

    @Query(DEL_CONSULTA)
    suspend fun deleteConsulta()

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
    suspend fun deleteEncuestaSeleccionado()

    @Query(DEL_RESPUESTA)
    suspend fun deleteRespuesta()

    @Query(DEL_INCIDENCIA)
    suspend fun deleteIncidencia()

    @Query(DEL_AFOTO)
    suspend fun deleteAFoto()

    @Query(DEL_AAUX)
    suspend fun deleteAAux()
}