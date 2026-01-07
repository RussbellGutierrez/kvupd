package com.upd.kvupd.data.model

import androidx.room.ColumnInfo

data class FlowCliente(
    @ColumnInfo(name = "cliente") val cliente: Int,
    @ColumnInfo(name = "nomcli") val nomcli: String,
    @ColumnInfo(name = "vendedor") val vendedor: Int,
    @ColumnInfo(name = "nomemp") val nomemp: String,
    @ColumnInfo(name = "secuencia") val secuencia: Int,
    @ColumnInfo(name = "ruta") val ruta: Int,
    @ColumnInfo(name = "baja") val baja: Int,
    @ColumnInfo(name = "fecha") val fecha: String
)