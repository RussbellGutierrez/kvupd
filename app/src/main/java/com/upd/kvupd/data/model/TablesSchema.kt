package com.upd.kvupd.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["codigo"])
data class TableConfiguracion(
    val codigo: Int,
    val empresa: Int,
    val esquema: Int,
    val fecha: String,
    val nombre: String,
    val codsuper: Int,
    val supervisor: String,
    val hfin: String,
    val hini: String,
    val ipp: String,
    val ips: String,
    val seguimiento: Int,
    val sucursal: Int,
    val tipo: String
)

@Entity(primaryKeys = ["idcliente", "ruta"])
data class TableCliente(
    val idcliente: Int,
    val nomcli: String,
    val ruta: Int,
    val empleado: Int,
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
    val codigo: Int,
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
    val ruta: Int,
    val corte: String,
    val longitud: Double,
    val latitud: Double,
    val visita: String
)

@Entity(primaryKeys = ["id", "pregunta"])
data class TableEncuesta(
    val id: Int,
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

@Entity(primaryKeys = ["cliente", "encuesta", "pregunta"])
data class TableRespuesta(
    val cliente: String,
    val fecha: String,
    val encuesta: Int,
    val pregunta: Int,
    val respuesta: String,
    val rutafoto: String,
    val foto: Int,
    val longitud: Double,
    val latitud: Double,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["fecha", "longitud", "latitud"])
data class TableSeguimiento(
    val fecha: String,
    val usuario: Int,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val bateria: Double,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["cliente"])
data class TableBaja(
    val cliente: Int,
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

@Entity(primaryKeys = ["idaux"])
data class TableAlta(
    val idaux: Int,             // PK interna local (autoincremental si quieres)
    val empleado: Int,          // empleado que generó el alta
    val fecha: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    var estado: String,
    val datos: Int,

    // 🔑 Nuevo campo
    val codigoGenerado: String  // Ej. "100001", "100002", ...
)

@Entity(primaryKeys = ["idaux"])
data class TableAltaDatos(
    val idaux: Int,
    val empleado: Int,
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
    val dniruta: String,
    val observacion: String,

    // 🔑 Nuevo control de sincronización
    var sincronizado: Boolean = false
)

@Entity(primaryKeys = ["clicodigo", "creado"])
data class TableBajaSupervisor(
    val sucursal: Int,
    val empleado: Int,
    val nombre: String,
    val creado: String,
    val dia: String,
    val motivo: String,
    val descripcion: String,
    val observacion: String,
    val confirmado: Int?,
    val fechaconf: String,
    val clicodigo: Int,
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

@Entity(primaryKeys = ["tipo", "fecha"])
data class TableIncidencia(
    var tipo: String,
    var usuario: Int,
    var observacion: String,
    var fecha: String
)

@Entity(primaryKeys = ["cliente", "documento"])
data class TableConsulta(
    val cliente: Int,
    val nombre: String,
    val domicilio: String,
    val longitud: Double,
    val latitud: Double,
    val telefono: String,
    val negocio: String,
    val canal: String,
    val anulado: Int,
    val documento: String,
    val ventas: Int
)

@Entity(primaryKeys = ["cliente", "fecha", "tipo"])
data class TableEstado(

    // ----------- Identificadores principales -----------
    val cliente: Int,       // ID del cliente afectado
    val empleado: Int,      // Empleado que realizó la acción
    val tipo: String,       // Tipo de estado: "VISITA", "BAJA", "ALTA", etc.
    val fecha: String,      // Fecha en la que se registró el evento

    // ----------- Datos de VISITA (antes en TableEstado) -----------
    val ruta: Int? = null,        // Ruta a la que pertenece (si aplica)
    val atendido: Int? = null,    // 0 = no atendido, 1 = atendido

    // ----------- Datos de BAJA (antes en TableBajaEstado) -----------
    val procede: Int? = null,         // 1 = procede, 0 = no procede
    val observacion: String? = null,  // Comentarios u observaciones
    val fechaconf: String? = null,    // Fecha de confirmación (si aplica)

    // ----------- Ubicación (usada en bajas o si se desea loguear visitas con coordenadas) -----------
    val longitud: Double? = null,     // Longitud GPS
    val latitud: Double? = null,      // Latitud GPS
    val precision: Double? = null,    // Precisión de la ubicación

    // 🔑----------- Control de sincronización -----------
    var sincronizado: Boolean = false // false = pendiente de enviar, true = enviado al servidor
)