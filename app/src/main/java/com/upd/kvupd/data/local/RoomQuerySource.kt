package com.upd.kvupd.data.local

import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomQuerySource @Inject constructor(
    private val query: QueryList
) {
    ////  ROOM
    suspend fun roomConfiguracion(): TableConfiguracion? =
        query.getConfiguracion()

    suspend fun roomClientes(): List<TableCliente> =
        query.getClientes()

    suspend fun roomDistritos(): List<TableDistrito> =
        query.getDistritos()

    suspend fun roomNegocios(): List<TableNegocio> =
        query.getNegocios()

    suspend fun roomRutas(): List<TableRuta> =
        query.getRutas()

    suspend fun roomAlta(idaux: String, fecha: String): TableAlta? =
        query.getAltaSpecific(idaux, fecha)

    suspend fun roomAltaDato(idaux: String, fecha: String): TableAltaDatos? =
        query.getAltaDatos(idaux, fecha)

    suspend fun roomHeaderEncuesta(): List<FlowHeaderEncuestas> =
        query.getHeadersEncuesta()

    suspend fun roomListaRutaFoto(hoy: String): List<String> =
        query.getListFotoRutas(hoy)

    ////  FLOW
    fun flowConfiguracion(): Flow<List<TableConfiguracion>> =
        query.flowConfiguracion()

    fun flowClientes(): Flow<List<FlowCliente>> =
        query.flowClientes()

    fun flowBajaSupervisor(): Flow<List<FlowBajaSupervisor>> =
        query.flowBajasSupervisor()

    fun flowAltas(): Flow<List<TableAlta>> =
        query.flowAltas()

    fun flowBajas(): Flow<List<TableBaja>> =
        query.flowBajas()

    fun flowRutas(): Flow<List<TableRuta>> =
        query.flowRutas()

    fun flowNegocios(): Flow<List<TableNegocio>> =
        query.flowNegocios()

    fun flowDistritos(): Flow<List<TableDistrito>> =
        query.flowDistritos()

    fun flowVendedores(): Flow<List<TableVendedor>> =
        query.flowVendedores()

    fun flowLastGPS(): Flow<TableSeguimiento?> =
        query.flowLastSeguimiento()

    fun flowPreguntasEncuesta(): Flow<List<TableEncuesta>> =
        query.flowPreguntaEncuestas()

    fun flowHeaderEncuesta(): Flow<List<FlowHeaderEncuestas>> =
        query.flowHeaderEncuestas()

    fun flowClientesExcluidos(id: String): Flow<List<TableCliente>> =
        query.flowClientesExcluidos(id)

    ////  UPDATE MANUAL
    suspend fun cleanAndSelectEncuesta(id: String) {
        query.reselectEncuesta(id)
    }

    suspend fun setSeleccionEncuesta(id: String) {
        query.setSeleccionEncuesta(id)
    }

    ////  TOTAL REGISTROS
    suspend fun countSeguimientoTotal() =
        query.seguimientoCount()

    suspend fun countAltaTotal() =
        query.altaCount()

    suspend fun countAltaDatosTotal() =
        query.altaDatoCount()

    suspend fun countBajaTotal() =
        query.bajaCount()

    suspend fun countBajaProcesadaTotal() =
        query.bajaProcesadaCount()

    suspend fun countRespuestaTotal() =
        query.respuestaCount()

    suspend fun countFotoTotal() =
        query.fotoCount()

    ////  SERVER
    suspend fun serverSeguimiento(sync: Boolean): List<TableSeguimiento> =
        query.serverSeguimiento(sync)

    suspend fun serverAltas(sync: Boolean): List<TableAlta> =
        query.serverAltas(sync)

    suspend fun serverAltaDatos(sync: Boolean): List<TableAltaDatos> =
        query.serverAltaDatos(sync)

    suspend fun serverBajas(sync: Boolean): List<TableBaja> =
        query.serverBajas(sync)

    suspend fun serverBajasProcesadas(sync: Boolean): List<TableBajaProcesada> =
        query.serverBajaProcesado(sync)

    suspend fun serverRespuestas(sync: Boolean): List<TableRespuesta> =
        query.serverRespuestas(sync)

    suspend fun serverFotos(sync: Boolean): List<TableFoto> =
        query.serverFotos(sync)

    ////  EXISTEN REGISTROS
    suspend fun hayPendientes(): Boolean {
        return query.hasSeguimientoPendiente() ||
                query.hasAltasPendientes() ||
                query.hasAltaDatosPendiente() ||
                query.hasBajasPendientes() ||
                query.hasBajaProcesadaPendiente() ||
                query.hasRespuestasPendientes() ||
                query.hasFotosPendientes()
    }

    suspend fun hayDatosParaLimpiar(hoy: String): Boolean {
        return query.needSeguimientoLimpiar(hoy) ||
                query.needAltasLimpiar(hoy) ||
                query.needAltaDatosLimpiar(hoy) ||
                query.needBajasLimpiar(hoy) ||
                query.needBajaProcesadaLimpiar(hoy) ||
                query.needRespuestasLimpiar(hoy) ||
                query.needFotosLimpiar(hoy)
    }
}