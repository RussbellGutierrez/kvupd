package com.upd.kvupd.domain

import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import okhttp3.RequestBody

interface JsObFunctions {
    fun jsonRegistrarEquipo(identificador: String, empresa: String): RequestBody
    fun jsonObjectConfiguracion(identificador: String): RequestBody
    fun jsonObjectClientes(
        item: TableConfiguracion,
        vendedor: Int? = null,
        fecha: String? = null
    ): RequestBody

    fun jsonObjectPedimap(item: TableConfiguracion): RequestBody
    fun jsonObjectBasico(item: TableConfiguracion): RequestBody
    fun jsonObjectSimple(item: TableConfiguracion): RequestBody
    fun jsonObjectSeguimiento(
        dato: TableConfiguracion,
        gps: TableSeguimiento,
        uuid: String
    ): RequestBody

    fun jsonObjectBajas(item: TableConfiguracion, baja: TableBaja): RequestBody
    fun jsonObjectBajasProcesadas(
        item: TableConfiguracion,
        bajaProcesada: TableBajaProcesada
    ): RequestBody

    fun jsonObjectAltas(item: TableConfiguracion, alta: TableAlta): RequestBody
    fun jsonObjectAltaDatos(item: TableConfiguracion, altaDatos: TableAltaDatos): RequestBody
    fun jsonObjectRespuesta(item: TableConfiguracion, respuesta: TableRespuesta): RequestBody
    fun jsonObjectFoto(item: TableConfiguracion, foto: TableFoto): RequestBody
    fun jsonObjectReporte(item: TableConfiguracion, linea: Int?, marca: Int?): RequestBody
}