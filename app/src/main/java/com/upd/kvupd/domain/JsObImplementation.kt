package com.upd.kvupd.domain

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.upd.kvupd.data.local.JsonObjectDataSource
import com.upd.kvupd.data.model.TableConfiguracion
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import javax.inject.Inject

class JsObImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jsonObjectDataSource: JsonObjectDataSource
) : JsObFunctions {
    override fun jsonRegistrarEquipo(identificador: String, empresa: String): RequestBody =
        jsonObjectDataSource.registrarEquipo(identificador, empresa)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun jsonObjectConfiguracion(identificador: String): RequestBody =
        jsonObjectDataSource.jsonRequestConfiguracion(identificador)

    override fun jsonObjectClientes(item: TableConfiguracion, fecha: String?): RequestBody =
        jsonObjectDataSource.jsonRequestClientes(item, fecha)

    override fun jsonObjectBasico(item: TableConfiguracion): RequestBody =
        jsonObjectDataSource.jsonRequestBasico(item)

    override fun jsonObjectSimple(item: TableConfiguracion): RequestBody =
        jsonObjectDataSource.jsonRequestSimple(item)
}