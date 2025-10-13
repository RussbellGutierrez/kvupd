package com.upd.kvupd.data.model

import androidx.room.ColumnInfo

data class FlowCliente(
    @ColumnInfo(name = "idcliente") val id: Int,
    @ColumnInfo(name = "nomcli") val nombre: String,
    @ColumnInfo(name = "empleado") val vendedor: Int,
    @ColumnInfo(name = "descripcion") val nomven: String,
    @ColumnInfo(name = "secuencia") val secuencia: Int,
    @ColumnInfo(name = "ruta") val ruta: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "encuestas") val encuestas: String,
    @ColumnInfo(name = "resuelto") val resuelto: Int
)