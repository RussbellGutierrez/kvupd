package com.upd.kvupd.data.model.cache

import androidx.room.Entity

@Entity(primaryKeys = ["idcliente", "ruta"])
data class TableCliente(
    val idcliente: String,
    val nomcli: String,
    val ruta: Int,
    val empleado: String,
    val domicli: String,
    val longitud: Double,
    val latitud: Double,
    val telefono: String,
    val negocio: String,
    val fecha: String,
    val secuencia: Int,
    val numcuit: String,
    val encuestas: String,
    val ventas: Int,
    val ventanio: Int
)

@Entity(primaryKeys = ["codigo"])
data class TableVendedor(
    val codigo: String,
    val descripcion: String,
    val cargo: String
)

@Entity(primaryKeys = ["codigo"])
data class TableDistrito(
    val codigo: String,
    val nombre: String
)

@Entity(primaryKeys = ["codigo"])
data class TableNegocio(
    val codigo: String,
    val nombre: String,
    val giro: String,
    val descripcion: String
)

@Entity(primaryKeys = ["ruta"])
data class TableRuta(
    val ruta: String,
    val corte: String,
    val longitud: Double,
    val latitud: Double,
    val visita: String
)

@Entity(primaryKeys = ["id", "pregunta"])
data class TableEncuesta(
    val id: String,
    val nombre: String,
    val foto: Boolean,
    val pregunta: Int,
    val descripcion: String,
    val tipo: String,
    val respuesta: String,
    val formato: String,
    val condicional: Boolean,
    val previa: Int,
    val eleccion: String,
    val necesaria: Boolean,

    // 👉 Nuevo campo para indicar la encuesta activa
    val seleccionada: Boolean = false
)

@Entity(primaryKeys = ["clicodigo", "creado"])
data class TableBajaSupervisor(
    val sucursal: Int,
    val empleado: String,
    val nombre: String,
    val creado: String,
    val dia: String,
    val motivo: String,
    val descripcion: String,
    val observacion: String,
    val confirmado: Int?,
    val fechaconf: String,
    val clicodigo: String,
    val clinombre: String,
    val documento: String,
    val direccion: String,
    val ruta: Int,
    val negocio: String,
    val canal: String,
    val pago: String,
    val visicooler: String,
    val clilongitud: Double,
    val clilatitud: Double,
    val hora: String,
    val precision: Double,
    val longitud: Double,
    val latitud: Double,
    val compra: String
)