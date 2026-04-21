package com.upd.kvupd.data.local

import com.upd.kvupd.data.local.cache.CacheQuery
import com.upd.kvupd.data.local.core.CoreQuery
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.cache.TableCliente
import com.upd.kvupd.data.model.cache.TableDistrito
import com.upd.kvupd.data.model.cache.TableEncuesta
import com.upd.kvupd.data.model.cache.TableNegocio
import com.upd.kvupd.data.model.cache.TableRuta
import com.upd.kvupd.data.model.cache.TableVendedor
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RoomQuerySource @Inject constructor(
    private val coreQuery: CoreQuery,
    private val cacheQuery: CacheQuery
) {
    ////  ROOM
    suspend fun roomConfiguracion(): TableConfiguracion? =
        coreQuery.getConfiguracion()

    suspend fun roomClientes(): List<TableCliente> =
        cacheQuery.getClientes()

    suspend fun roomDistritos(): List<TableDistrito> =
        cacheQuery.getDistritos()

    suspend fun roomNegocios(): List<TableNegocio> =
        cacheQuery.getNegocios()

    suspend fun roomRutas(): List<TableRuta> =
        cacheQuery.getRutas()

    suspend fun roomAlta(idaux: String, fecha: String): TableAlta? =
        coreQuery.getAltaSpecific(idaux, fecha)

    suspend fun roomAltaDato(idaux: String, fecha: String): TableAltaDatos? =
        coreQuery.getAltaDatos(idaux, fecha)

    suspend fun roomHeaderEncuesta(): List<FlowHeaderEncuestas> =
        cacheQuery.getHeadersEncuesta()

    suspend fun roomListaRutaFoto(hoy: String): List<String> =
        coreQuery.getListFotoRutas(hoy)

    ////  FLOW
    fun flowConfiguracion(): Flow<List<TableConfiguracion>> =
        coreQuery.flowConfiguracion()

    fun flowCombineClientes(): Flow<List<FlowCliente>> =
        combine(
            cacheQuery.flowClientes(),
            cacheQuery.flowVendedores(),
            coreQuery.flowBajas()
        ) { clientes, vendedores, bajas ->

            val vendedoresMap = vendedores.associateBy { it.codigo }

            val bajasCount = bajas
                .groupingBy { it.cliente }
                .eachCount()

            clientes
                .map { c ->
                    FlowCliente(
                        cliente = c.idcliente,
                        nomcli = c.nomcli,
                        vendedor = c.empleado.toIntOrNull() ?: 0,
                        nomemp = vendedoresMap[c.empleado]?.descripcion ?: "null",
                        domicilio = c.domicli,
                        longitud = c.longitud,
                        latitud = c.latitud,
                        ruta = c.ruta,
                        baja = bajasCount[c.idcliente] ?: 0,
                        ventas = c.ventas,
                        compras = c.ventanio,
                        fecha = c.fecha,
                        negocio = c.negocio
                    )
                }
                .sortedWith(
                    compareBy<FlowCliente> { it.fecha }
                        .thenBy { it.nomcli }
                )
        }

    fun flowCombineBajasSupervisor(): Flow<List<FlowBajaSupervisor>> =
        combine(
            cacheQuery.flowBajasSupervisor(),
            coreQuery.flowBajasProcesadas()
        ) { bajasSupervisor, bajasProcesadas ->

            val procesadasMap = bajasProcesadas.associateBy {
                "${it.empleado}_${it.cliente}"
            }

            bajasSupervisor
                .map { b ->

                    val key = "${b.empleado}_${b.clicodigo}"
                    val procesada = procesadasMap[key]

                    FlowBajaSupervisor(
                        vendedor = b.empleado,
                        vendnom = b.nombre,
                        creacion = b.creado,
                        motivo = b.motivo.toIntOrNull() ?: 0,
                        cliente = b.clicodigo,
                        nombre = b.clinombre,
                        direccion = b.direccion,
                        canal = b.canal,
                        observacion = b.observacion,
                        negocio = b.negocio,
                        pago = b.pago,
                        compra = b.compra,
                        longitud = b.clilongitud,
                        latitud = b.clilatitud,
                        procede = procesada?.procede
                    )
                }
                .sortedByDescending { it.creacion }
        }

    fun flowCombineClientesExcluidos(encuestaId: Int): Flow<List<TableCliente>> =
        combine(
            cacheQuery.flowClientes(),
            coreQuery.flowRespuestas()
        ) { clientes, respuestas ->

            val respondidos = respuestas
                .filter { it.encuesta == encuestaId }
                .map { it.cliente }
                .toSet()

            clientes.filter { c ->

                val encuestaEnCliente =
                    ",${c.encuestas},".contains(",$encuestaId,")

                !encuestaEnCliente &&
                        c.idcliente !in respondidos
            }
        }

    fun flowAltas(): Flow<List<TableAlta>> =
        coreQuery.flowAltas()

    fun flowBajas(): Flow<List<TableBaja>> =
        coreQuery.flowBajas()

    fun flowRutas(): Flow<List<TableRuta>> =
        cacheQuery.flowRutas()

    fun flowNegocios(): Flow<List<TableNegocio>> =
        cacheQuery.flowNegocios()

    fun flowDistritos(): Flow<List<TableDistrito>> =
        cacheQuery.flowDistritos()

    fun flowVendedores(): Flow<List<TableVendedor>> =
        cacheQuery.flowVendedores()

    fun flowLastGPS(): Flow<TableSeguimiento?> =
        coreQuery.flowLastSeguimiento()

    fun flowPreguntasEncuesta(): Flow<List<TableEncuesta>> =
        cacheQuery.flowPreguntaEncuestas()

    fun flowHeaderEncuesta(): Flow<List<FlowHeaderEncuestas>> =
        cacheQuery.flowHeaderEncuestas()

    ////  UPDATE MANUAL
    suspend fun cleanAndSelectEncuesta(id: String) {
        cacheQuery.reselectEncuesta(id)
    }

    suspend fun setSeleccionEncuesta(id: String) {
        cacheQuery.setSeleccionEncuesta(id)
    }

    ////  TOTAL REGISTROS
    suspend fun countSeguimientoTotal() =
        coreQuery.seguimientoCount()

    suspend fun countAltaTotal() =
        coreQuery.altaCount()

    suspend fun countAltaDatosTotal() =
        coreQuery.altaDatoCount()

    suspend fun countBajaTotal() =
        coreQuery.bajaCount()

    suspend fun countBajaProcesadaTotal() =
        coreQuery.bajaProcesadaCount()

    suspend fun countRespuestaTotal() =
        coreQuery.respuestaCount()

    suspend fun countFotoTotal() =
        coreQuery.fotoCount()

    ////  SERVER
    suspend fun serverSeguimiento(sync: Boolean): List<TableSeguimiento> =
        coreQuery.serverSeguimiento(sync)

    suspend fun serverAltas(sync: Boolean): List<TableAlta> =
        coreQuery.serverAltas(sync)

    suspend fun serverAltaDatos(sync: Boolean): List<TableAltaDatos> =
        coreQuery.serverAltaDatos(sync)

    suspend fun serverBajas(sync: Boolean): List<TableBaja> =
        coreQuery.serverBajas(sync)

    suspend fun serverBajasProcesadas(sync: Boolean): List<TableBajaProcesada> =
        coreQuery.serverBajaProcesado(sync)

    suspend fun serverRespuestas(sync: Boolean): List<TableRespuesta> =
        coreQuery.serverRespuestas(sync)

    suspend fun serverFotos(sync: Boolean): List<TableFoto> =
        coreQuery.serverFotos(sync)

    ////  EXISTEN REGISTROS
    suspend fun hayPendientes(): Boolean {
        return coreQuery.hasSeguimientoPendiente() ||
                coreQuery.hasAltasPendientes() ||
                coreQuery.hasAltaDatosPendiente() ||
                coreQuery.hasBajasPendientes() ||
                coreQuery.hasBajaProcesadaPendiente() ||
                coreQuery.hasRespuestasPendientes() ||
                coreQuery.hasFotosPendientes()
    }

    suspend fun hayDatosParaLimpiar(hoy: String): Boolean {
        return coreQuery.needSeguimientoLimpiar(hoy) ||
                coreQuery.needAltasLimpiar(hoy) ||
                coreQuery.needAltaDatosLimpiar(hoy) ||
                coreQuery.needBajasLimpiar(hoy) ||
                coreQuery.needBajaProcesadaLimpiar(hoy) ||
                coreQuery.needRespuestasLimpiar(hoy) ||
                coreQuery.needFotosLimpiar(hoy)
    }
}