package com.upd.kvupd.data.local

import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableEstado
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

    suspend fun roomVendedores(): List<TableVendedor> =
        query.getVendedores()

    suspend fun roomDistritos(): List<TableDistrito> =
        query.getDistritos()

    suspend fun roomNegocios(): List<TableNegocio> =
        query.getNegocios()

    suspend fun roomRutas(): List<TableRuta> =
        query.getRutas()

    suspend fun roomEncuestas(): List<TableEncuesta> =
        query.getEncuestas()

    suspend fun roomBajaEspecifica(cliente: String): TableBaja? =
        query.getBajaCliente(cliente)

    ////  FLOW
    fun flowConfiguracion(): Flow<List<TableConfiguracion>> =
        query.flowConfiguracion()

    fun flowClientes(): Flow<List<FlowCliente>> =
        query.flowClientes()

    fun flowAltas(): Flow<List<TableAlta>> =
        query.flowAltas()

    ////  SERVER
    suspend fun serverSeguimiento(sync: Boolean): List<TableSeguimiento> =
        query.serverSeguimiento(sync)

    suspend fun serverAltas(sync: Boolean): List<TableAlta> =
        query.serverAltas(sync)

    suspend fun serverAltaDatos(sync: Boolean): List<TableAltaDatos> =
        query.serverAltaDatos(sync)

    suspend fun serverBajas(sync: Boolean): List<TableBaja> =
        query.serverBajas(sync)

    suspend fun serverEstados(sync: Boolean): List<TableEstado> =
        query.serverEstados(sync)

    suspend fun serverRespuestas(sync: Boolean): List<TableRespuesta> =
        query.serverRespuestas(sync)

    suspend fun serverFotos(sync: Boolean): List<TableRespuesta> =
        query.serverFotos(sync)
}