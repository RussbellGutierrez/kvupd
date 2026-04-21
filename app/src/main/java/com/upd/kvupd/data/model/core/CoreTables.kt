package com.upd.kvupd.data.model.core

import androidx.room.Entity

@Entity(primaryKeys = ["codigo"])
data class TableConfiguracion(
    val codigo: String,
    val empresa: Int,
    val esquema: Int,
    val fecha: String,
    val nombre: String,
    val codsuper: Int,
    val supervisor: String,
    val horafin: String,
    val horainicio: String,
    val ipp: String,
    val ips: String,
    val seguimiento: Int,
    val sucursal: Int,
    val tipo: String
)

@Entity(primaryKeys = ["fecha", "longitud", "latitud"])
data class TableSeguimiento(
    val fecha: String,
    val usuario: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val bateria: Double,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["fecha", "idaux"])
data class TableAlta(
    val idaux: String,             // Codigo usuario + dia + hora
    val empleado: String,          // empleado que generó el alta
    val fecha: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val datos: Int,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["fecha", "idaux"])
data class TableAltaDatos(
    val fecha: String,
    val idaux: String,
    val empleado: String,
    val tipo: String,
    val razon: String,
    val nombre: String,
    val appaterno: String,
    val apmaterno: String,
    val ruc: String,
    val dnice: String,
    val tipodocu: String,
    val movil1: String,
    val movil2: String,
    val correo: String,
    val via: String,
    val direccion: String,
    val manzana: String,
    val zona: String,
    val zonanombre: String,
    val ubicacion: String,
    val numero: String,
    val distrito: String,
    val giro: String,
    val ruta: String,
    val secuencia: String,
    val observacion: String,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["cliente"])
data class TableBaja(
    val cliente: String,
    val nombre: String,
    val motivo: Int,
    val comentario: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val fecha: String,
    val anulado: Int,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["empleado", "cliente", "fecha"])
data class TableBajaProcesada(
    var empleado: String,
    var cliente: String,
    var procede: Int,
    var fecha: String,
    var precision: Double,
    var longitud: Double,
    var latitud: Double,
    var fechaconfirmacion: String,
    var observacion: String,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["cliente", "encuesta", "pregunta"])
data class TableRespuesta(
    val cliente: String,
    val fecha: String,
    val encuesta: Int,
    val pregunta: Int,
    val respuesta: String,
    val longitud: Double,
    val latitud: Double,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["cliente", "encuesta"])
data class TableFoto(
    val cliente: String,
    val fecha: String,
    val encuesta: Int,
    val rutafoto: String,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)