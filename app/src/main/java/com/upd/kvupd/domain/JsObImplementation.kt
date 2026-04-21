package com.upd.kvupd.domain

import android.content.Context
import com.upd.kvupd.data.local.JsonObjectDataSource
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import javax.inject.Inject

class JsObImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jsonObjectDataSource: JsonObjectDataSource
) : JsObFunctions {
    override fun jsonRegistrarEquipo(identificador: String, empresa: String): RequestBody =
        jsonObjectDataSource.registrarEquipo(identificador, empresa)

    override fun jsonObjectConfiguracion(identificador: String): RequestBody =
        jsonObjectDataSource.jsonRequestConfiguracion(identificador)

    override fun jsonObjectClientes(
        item: TableConfiguracion,
        vendedor: Int?,
        fecha: String?
    ): RequestBody =
        jsonObjectDataSource.jsonRequestClientes(item, vendedor, fecha)

    override fun jsonObjectPedimap(item: TableConfiguracion): RequestBody =
        jsonObjectDataSource.jsonRequestPedimap(item)

    override fun jsonObjectBasico(item: TableConfiguracion): RequestBody =
        jsonObjectDataSource.jsonRequestBasico(item)

    override fun jsonObjectSimple(item: TableConfiguracion): RequestBody =
        jsonObjectDataSource.jsonRequestSimple(item)

    override fun jsonObjectSeguimiento(
        dato: TableConfiguracion,
        gps: TableSeguimiento,
        uuid: String
    ): RequestBody =
        jsonObjectDataSource.jsonRequestSeguimiento(dato, gps, uuid)

    override fun jsonObjectBajas(item: TableConfiguracion, baja: TableBaja): RequestBody =
        jsonObjectDataSource.jsonRequestBajas(item, baja)

    override fun jsonObjectBajasProcesadas(
        item: TableConfiguracion,
        bajaProcesada: TableBajaProcesada
    ): RequestBody =
        jsonObjectDataSource.jsonRequestBajasProcesadas(item, bajaProcesada)

    override fun jsonObjectAltas(item: TableConfiguracion, alta: TableAlta): RequestBody =
        jsonObjectDataSource.jsonRequestAlta(item, alta)

    override fun jsonObjectAltaDatos(
        item: TableConfiguracion,
        altaDatos: TableAltaDatos
    ): RequestBody =
        jsonObjectDataSource.jsonRequestAltaDatos(item, altaDatos)

    override fun jsonObjectRespuesta(
        item: TableConfiguracion,
        respuesta: TableRespuesta
    ): RequestBody =
        jsonObjectDataSource.jsonRequestRespuesta(item, respuesta)

    override fun jsonObjectFoto(item: TableConfiguracion, foto: TableFoto): RequestBody =
        jsonObjectDataSource.jsonRequestFoto(item, foto)

    override fun jsonObjectReporte(
        item: TableConfiguracion,
        linea: Int?,
        marca: Int?
    ): RequestBody =
        jsonObjectDataSource.jsonRequestReport(item, linea, marca)
}