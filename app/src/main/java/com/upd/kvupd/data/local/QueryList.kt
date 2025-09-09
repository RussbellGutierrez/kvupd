package com.upd.kvupd.data.local

import androidx.room.Dao
import androidx.room.Query
import com.upd.kvupd.data.model.QueryConstant.GET_CONFIGURACION
import com.upd.kvupd.data.model.TableConfiguracion

@Dao
interface QueryList {

    @Query(GET_CONFIGURACION)
    suspend fun getConfiguracion(): TableConfiguracion?

    /*@Query(GET_CONFIG)
    fun getObsConfig(): Flow<List<TConfiguracion>>

    @Query(GET_SESION)
    fun getObsSession(): Flow<TSesion?>

    @Query(GET_ROW_CLIENTES)
    fun getRowClientes(): Flow<List<RowCliente>>

    @Query(GET_LAST_LOCATION)
    fun getLastLocation(): Flow<List<TSeguimiento>?>

    @Query(GET_MARKERS)
    fun getMarkers(observacion: String): Flow<List<MarkerMap>>

    @Query(GET_ALTAS)
    fun getAltas(): Flow<List<TAlta>>

    @Query(GET_NEGOCIOS)
    fun getObsNegocios(): Flow<List<TNegocio>>

    @Query(GET_DISTRITOS)
    fun getObsDistritos(): Flow<List<TDistrito>>

    @Query(GET_BAJA)
    fun getBajas(): Flow<List<TBaja>>

    @Query(GET_ROW_BAJAS)
    fun getRowBajas(): Flow<List<RowBaja>>

    @Query(GET_RUTAS)
    fun getObsRutas(): Flow<List<TRutas>>

    @Query(GET_INCIDENCIA)
    fun getIncidencias(): Flow<List<TIncidencia>>

    @Query(GET_SESION)
    suspend fun getSesion(): TSesion?



    @Query(GET_CLIENTES)
    suspend fun getClientes(): List<TClientes>

    @Query(GET_EMPLEADOS)
    suspend fun getEmpleados(): List<TEmpleados>

    @Query(GET_DISTRITOS)
    suspend fun getDistrito(): List<TDistrito>

    @Query(GET_NEGOCIOS)
    suspend fun getNegocio(): List<TNegocio>

    @Query(GET_RUTAS)
    suspend fun getRutas(): List<TRutas>

    @Query(GET_CABE_ENCUESTAS)
    suspend fun getCabeEncuesta(): List<Cabecera>

    @Query(GET_ENCUESTA)
    suspend fun getEncuesta(): List<TEncuesta>

    @Query(GET_CONSULTA)
    suspend fun getConsulta(numero: String, nombre: String): List<TConsulta>

    @Query(GET_DATA_CLIENTE)
    suspend fun getDataCliente(cliente: String, observacion: String): List<DataCliente>

    @Query(GET_BAJA_SPECIFIC)
    suspend fun getBajaCliente(cliente: String): TBaja?

    @Query(GET_DATA_ALTA)
    suspend fun getDataAlta(alta: String): DataAlta

    @Query(GET_ALTADATOS)
    suspend fun getAltaDatoSpecific(alta: String): TADatos?

    @Query(GET_LAST_AUX)
    suspend fun getLastAux(): Int?

    @Query(GET_BAJA_SUPER)
    suspend fun getBajaSuper(codigo: String, fecha: String): TBajaSuper

    @Query(GET_SELECCION)
    suspend fun getSeleccionado(): TEncuestaSeleccionado?

    @Query(GET_RESPUESTA_CLIENTE)
    suspend fun getRespuesta(cliente: String): RespuestaCliente?

    @Query(GET_RESPUESTA_HISTORICO)
    suspend fun getRespuestaH(cliente: String): RespuestaHistorico

    /*  Dao send data to server    */
    @Query(GET_SEGUIMIENTO_SERVER)
    suspend fun seguimientoServer(estado: String): List<TSeguimiento>

    @Query(GET_VISITA_SERVER)
    suspend fun visitaServer(estado: String): List<TVisita>

    @Query(GET_ALTA_SERVER)
    suspend fun altaServer(estado: String): List<TAlta>

    @Query(GET_ALTADATO_SERVER)
    suspend fun altadatosServer(estado: String): List<TADatos>

    @Query(GET_BAJA_SERVER)
    suspend fun bajaServer(estado: String): List<TBaja>

    @Query(GET_BAJAESTADO_SERVER)
    suspend fun bajaestadoServer(estado: String): List<TBEstado>

    @Query(GET_RESPUESTA_SERVER)
    suspend fun respuestaServer(estado: String): List<TRespuesta>

    @Query(GET_FOTO_SERVER)
    suspend fun fotoServer(estado: String): List<TRespuesta>

    @Query(GET_AFOTO_SERVER)
    suspend fun altaFotoServer(estado: String): List<TAFoto>*/
}