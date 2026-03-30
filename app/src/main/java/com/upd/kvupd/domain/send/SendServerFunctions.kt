package com.upd.kvupd.domain.send

import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.ui.sealed.ResultadoApi

interface SendServerFunctions {
    suspend fun enviarBaja(item: TableBaja): ResultadoApi<Unit>
    suspend fun enviarBajaProcesada(item: TableBajaProcesada): ResultadoApi<Unit>
    suspend fun enviarAlta(item: TableAlta): ResultadoApi<Unit>
    suspend fun enviarAltaDatos(item: TableAltaDatos): ResultadoApi<Unit>
    suspend fun enviarRespuesta(item: List<TableRespuesta>): ResultadoApi<Unit>
    suspend fun enviarFoto(item: TableFoto): ResultadoApi<Unit>
}