package com.upd.kvupd.domain

import com.upd.kvupd.data.model.TableConfiguracion
import okhttp3.RequestBody

interface JsObFunctions {
    fun jsonRegistrarEquipo(identificador: String, empresa: String): RequestBody
    fun jsonObjectConfiguracion(identificador: String): RequestBody
    fun jsonObjectClientes(item: TableConfiguracion, vendedor: Int? = null, fecha: String? = null): RequestBody
    fun jsonObjectPedimap(item: TableConfiguracion): RequestBody
    fun jsonObjectBasico(item: TableConfiguracion): RequestBody
    fun jsonObjectSimple(item: TableConfiguracion): RequestBody
}