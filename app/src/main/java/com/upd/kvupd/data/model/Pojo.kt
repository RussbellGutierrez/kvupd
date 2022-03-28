package com.upd.kvupd.data.model

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

data class LocationAlta(
    @ColumnInfo(name = "idaux") val idaux: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "longitud") val longitud: Double,
    @ColumnInfo(name = "latitud") val latitud: Double,
    @ColumnInfo(name = "precision") val precision: Double,
    @ColumnInfo(name = "estado") val estado: String
)

data class MiniUpdAlta(
    @ColumnInfo(name = "idaux") val idaux: Int,
    @ColumnInfo(name = "datos") val datos: Int
)

data class MiniUpdBaja(
    @ColumnInfo(name = "cliente") val cliente: Int,
    @ColumnInfo(name = "anulado") val anulado: Int,
    @ColumnInfo(name = "estado") val estado: String
)

data class RowBaja(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "direccion") val direccion: String,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "dia") val dia: String,
    @ColumnInfo(name = "motivo") val motivo: String,
    @ColumnInfo(name = "negocio") val negocio: String,
    @ColumnInfo(name = "procede") val procede: Int
)

data class Cabecera(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "foto") val foto: Boolean
)

data class Respuesta(
    val encuesta: Int,
    val pregunta: Int,
    val respuesta: String,
    val ruta: String
)