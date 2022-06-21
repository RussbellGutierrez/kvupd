package com.upd.kvupd.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.upd.kvupd.utils.Constant.CONF
import javax.annotation.Nullable

@Entity(primaryKeys = ["codigo"])
data class TSesion(
    val codigo: Int,
    val empresa: Int,
    val esquema: Int,
    val sucursal: Int,
    val fecha: String,
    val hini: String,
    val hfin: String
)

fun Config.asTSesion(): TSesion =
    TSesion(this.codigo, this.empresa, this.esquema, this.sucursal, this.fecha, this.hini, this.hfin)

@Entity(primaryKeys = ["codigo"])
data class TConfiguracion(
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

fun Config.asTConfig(): TConfiguracion =
    TConfiguracion(
        this.codigo,
        this.empresa,
        this.esquema,
        this.fecha,
        this.nombre,
        this.codsuper,
        this.supervisor,
        this.hfin,
        this.hini,
        this.ipp,
        this.ips,
        this.seguimiento,
        this.sucursal,
        this.tipo
    )

@Entity(primaryKeys = ["idcliente", "ruta"])
data class TClientes(
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
    val encuestas: String
)

fun List<TClientes>.asClienteList(): List<Cliente> = this.map {
    Cliente(
        it.idcliente,
        it.nomcli,
        it.ruta,
        it.empleado,
        it.domicli,
        it.longitud,
        it.latitud,
        it.telefono,
        it.negocio,
        it.fecha,
        it.secuencia,
        it.numcuit,
        it.encuestas
    )
}

fun Cliente.asTCliente(): TClientes =
    TClientes(
        this.codigo,
        this.cliente,
        this.ruta,
        this.vendedor,
        this.domicilio,
        this.longitud,
        this.latitud,
        this.telefono,
        this.negocio,
        this.fecha,
        this.secuencia,
        this.numcuit,
        this.encuestas
    )

@Entity(primaryKeys = ["codigo"])
data class TEmpleados(
    val codigo: Int,
    val descripcion: String,
    val cargo: String
)

fun List<TEmpleados>.asEmpleadoList(): List<Vendedor> = this.map {
    Vendedor(it.codigo, it.descripcion, it.cargo)
}

fun Vendedor.asTEmpleado(): TEmpleados =
    TEmpleados(this.codigo, this.descripcion, this.cargo)

@Entity(primaryKeys = ["codigo"])
data class TDistrito(
    val codigo: String,
    val nombre: String
)

fun List<TDistrito>.asDistritoList(): List<Combo> = this.map {
    Combo(it.codigo, it.nombre)
}

fun Combo.asTDistrito(): TDistrito =
    TDistrito(this.codigo, this.nombre)

@Entity(primaryKeys = ["codigo"])
data class TNegocio(
    val codigo: String,
    val nombre: String
)

fun List<TNegocio>.asNegocioList(): List<Combo> = this.map {
    Combo(it.codigo, it.nombre)
}

fun Combo.asTNegocio(): TNegocio =
    TNegocio(this.codigo, this.nombre)

fun List<Combo>.asSpinner(): List<String> = this.map {
    "${it.codigo} - ${it.nombre}"
}

@Entity(primaryKeys = ["id","pregunta"])
data class TEncuesta(
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
    val necesaria: Boolean
)

fun List<TEncuesta>.asEncuestaList(): List<Encuesta> = this.map {
    Encuesta(
        it.id,
        it.nombre,
        it.foto,
        it.pregunta,
        it.descripcion,
        it.tipo,
        it.respuesta,
        it.formato,
        it.condicional,
        it.previa,
        it.eleccion,
        it.necesaria
    )
}

fun Encuesta.asTEncuesta(): TEncuesta =
    TEncuesta(
        this.id,
        this.nombre,
        this.foto,
        this.pregunta,
        this.descripcion,
        this.tipo,
        this.respuesta,
        this.formato,
        this.condicional,
        this.previa,
        this.eleccion,
        this.necesaria
    )

@Entity(primaryKeys = ["id"])
data class TEncuestaSeleccionado(
    val id: Int,
    val encuesta: Int,
    val foto: Boolean
)

@Entity(primaryKeys = ["cliente", "encuesta", "pregunta"])
data class TRespuesta(
    val cliente: Int,
    val fecha: String,
    val encuesta: Int,
    val pregunta: Int,
    val respuesta: String,
    val rutafoto: String,
    var estado: String
)

@Entity(primaryKeys = ["ruta"])
data class TRutas(
    val ruta: Int,
    val corte: String,
    val longitud: Double,
    val latitud: Double
)

fun List<TRutas>.asRutaList(): List<Ruta> = this.map {
    Ruta(
        it.ruta,
        it.corte,
        it.longitud,
        it.latitud
    )
}

fun Ruta.asTRutas(): TRutas =
    TRutas(
        this.ruta,
        this.coords,
        this.longitud,
        this.latitud
    )

@Entity(primaryKeys = ["idcliente", "ruta"])
data class TEstado(
    val idcliente: Int,
    val empleado: Int,
    val ruta: Int,
    val atendido: Int
)

fun TVisita.asTEstado(ruta: Int): TEstado =
    TEstado(this.cliente, this.usuario, ruta, 1)

fun TBaja.asTEstado(ruta: Int): TEstado =
    TEstado(this.cliente, CONF.codigo, ruta, 2)

@Entity(primaryKeys = ["fecha", "longitud", "latitud"])
data class TSeguimiento(
    val fecha: String,
    val usuario: Int,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val bateria: Double,
    var estado: String
)

@Entity(primaryKeys = ["cliente"])
data class TVisita(
    val cliente: Int,
    val fecha: String,
    val usuario: Int,
    val longitud: Double,
    val latitud: Double,
    val observacion: Int,
    val precision: Double,
    var estado: String
)

@Entity(primaryKeys = ["cliente"])
data class TBaja(
    val cliente: Int,
    val nombre: String,
    val motivo: Int,
    val comentario: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val fecha: String,
    val anulado: Int,
    var estado: String
)

@Entity(primaryKeys = ["idaux"])
data class TAlta(
    val idaux: Int,
    val fecha: String,
    val empleado: Int,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    var estado: String,
    val datos: Int
)

@Entity(primaryKeys = ["idaux"])
data class TADatos(
    val idaux: Int,
    val empleado: Int,
    val tipo: String,
    val razon: String,
    val nombre: String,
    val appaterno: String,
    val apmaterno: String,
    val documento: String,
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
    var estado: String
)

@Entity(primaryKeys = ["clicodigo", "creado"])
data class TBajaSuper(
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

fun BajaSupervisor.asTBajaSuper(): TBajaSuper =
    TBajaSuper(
        this.sucursal,
        this.empleado,
        this.nombre,
        this.creado,
        this.dia,
        this.motivo,
        this.descripcion,
        this.observacion,
        this.confirmado,
        this.fechaconf,
        this.cliente.codigo,
        this.cliente.nombre,
        this.cliente.documento,
        this.cliente.direccion,
        this.cliente.ruta,
        this.cliente.negocio,
        this.cliente.canal,
        this.cliente.pago,
        this.cliente.visicooler,
        this.cliente.longitud,
        this.cliente.latitud,
        this.hora,
        this.precision,
        this.longitud,
        this.latitud,
        this.compra
    )

@Entity(primaryKeys = ["cliente", "fecha"])
data class TBEstado(
    var empleado: Int,
    var cliente: Int,
    var procede: Int,
    var fecha: String,
    var precision: Double,
    var longitud: Double,
    var latitud: Double,
    var fechaconf: String,
    var observacion: String,
    var estado: String
)