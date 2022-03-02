package com.upd.kventas.data.model

import androidx.room.ColumnInfo

data class RowCliente(
    @ColumnInfo(name = "idcliente") val id: Int,
    @ColumnInfo(name = "nomcli") val nombre: String,
    @ColumnInfo(name = "empleado") val vendedor: Int,
    @ColumnInfo(name = "descripcion") val nomven: String,
    @ColumnInfo(name = "secuencia") val secuencia: Int,
    @ColumnInfo(name = "ruta") val ruta: Int,
    @ColumnInfo(name = "atendido") val atendido: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "encuestas") val encuestas: String
)

data class MarkerMap(
    @ColumnInfo(name = "idcliente") val id: Int,
    @ColumnInfo(name = "longitud") val longitud: Double,
    @ColumnInfo(name = "latitud") val latitud: Double,
    @ColumnInfo(name = "observacion") val observacion: Int,
    @ColumnInfo(name = "atendido") val atendido: Int
)

data class DataCliente(
    @ColumnInfo(name = "idcliente") val id: Int,
    @ColumnInfo(name = "nomcli") val nombre: String,
    @ColumnInfo(name = "domicli") val domicilio: String,
    @ColumnInfo(name = "ruta") val ruta: Int,
    @ColumnInfo(name = "negocio") val negocio: String,
    @ColumnInfo(name = "telefono") val telefono: String
)