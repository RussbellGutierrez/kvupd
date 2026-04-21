package com.upd.kvupd.domain.send

import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import com.upd.kvupd.ui.sealed.ResultadoApi

interface SendServerFunctions {
    suspend fun enviarBaja(item: TableBaja): ResultadoApi<Unit>
    suspend fun enviarBajaProcesada(item: TableBajaProcesada): ResultadoApi<Unit>
    suspend fun enviarAlta(item: TableAlta): ResultadoApi<Unit>
    suspend fun enviarAltaDatos(item: TableAltaDatos): ResultadoApi<Unit>
    suspend fun enviarRespuesta(item: List<TableRespuesta>): ResultadoApi<Unit>
    suspend fun enviarFoto(item: TableFoto): ResultadoApi<Unit>
    suspend fun enviarSeguimiento(item: TableSeguimiento, identificador: String): ResultadoApi<Unit>
}