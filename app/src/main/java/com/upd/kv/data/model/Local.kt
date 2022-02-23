package com.upd.kv.data.model

import androidx.room.Entity
import com.upd.kv.utils.Constant.CONF

@Entity(primaryKeys = ["codigo", "empresa", "sucursal", "esquema"])
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

fun List<TConfiguracion>.asConfigList(): List<Config> = this.map {
    Config(
        it.codigo,
        it.empresa,
        it.esquema,
        it.fecha,
        it.nombre,
        it.codsuper,
        it.supervisor,
        it.hfin,
        it.hini,
        it.ipp,
        it.ips,
        it.seguimiento,
        it.sucursal,
        it.tipo
    )
}

fun Config.asTConfig(): TConfiguracion =
    TConfiguracion(
        this.codigo,
        this.empresa,
        this.esquema,
        this.fecha,
        this.nombre,
        this.codsuper,
        this.supervisor,
        this.hini,
        this.hfin,
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

@Entity(primaryKeys = ["id"])
data class TEncuesta(
    val id: String,
    val nombre: String,
    val foto: Boolean,
    val pregunta: String,
    val descripcion: String,
    val tipo: String,
    val respuesta: String,
    val formato: String,
    val condicional: Boolean,
    val previa: Int,
    val eleccion: String
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
        it.eleccion
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
        this.eleccion
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
    val estado: String
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
    val estado: String
)

@Entity(primaryKeys = ["cliente"])
data class TBaja(
    val cliente: Int,
    val motivo: Int,
    val comentario: String,
    val longitud: Double,
    val latitud: Double,
    val precision: Double,
    val fecha: String,
    val anulado: Int,
    val estado: String
)

