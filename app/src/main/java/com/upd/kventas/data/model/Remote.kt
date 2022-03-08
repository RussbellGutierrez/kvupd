package com.upd.kventas.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class JObj(
    @Json(name = "data") val jobl: List<JSONObject>
)

@JsonClass(generateAdapter = true)
data class JConfig(
    @Json(name = "data") val jobl: List<Config>
)

@JsonClass(generateAdapter = true)
data class Config(
    @Json(name = "codigo") val codigo: Int,
    @Json(name = "empresa") val empresa: Int,
    @Json(name = "esquema") val esquema: Int,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "super") val codsuper: Int,
    @Json(name = "supervisor") val supervisor: String,
    @Json(name = "horafinal") val hfin: String,
    @Json(name = "horainicio") val hini: String,
    @Json(name = "ipprincipal") val ipp: String,
    @Json(name = "ipsecundaria") val ips: String,
    @Json(name = "seguimiento") val seguimiento: Int,
    @Json(name = "sucursal") val sucursal: Int,
    @Json(name = "tipo") val tipo: String
)

@JsonClass(generateAdapter = true)
data class JCliente(
    @Json(name = "data") val jobl: List<Cliente>
)

@JsonClass(generateAdapter = true)
data class Cliente(
    @Json(name = "idcliente") val codigo: Int,
    @Json(name = "nomcli") val cliente: String,
    @Json(name = "ruta") val ruta: Int,
    @Json(name = "c_perso") val vendedor: Int,
    @Json(name = "domicli") val domicilio: String,
    @Json(name = "XCoord") val longitud: Double,
    @Json(name = "YCoord") val latitud: Double,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "secuencia") val secuencia: Int,
    @Json(name = "numcuit") val numcuit: String,
    @Json(name = "encuestas") val encuestas: String
)

@JsonClass(generateAdapter = true)
data class JVendedores(
    @Json(name = "data") val jobl: List<Vendedor>
)

@JsonClass(generateAdapter = true)
data class Vendedor(
    @Json(name = "value") val codigo: Int,
    @Json(name = "name") val descripcion: String,
    @Json(name = "type") val cargo: String
)

@JsonClass(generateAdapter = true)  //    Modelado que sirve para distrito y negocio
data class JCombo(
    @Json(name = "data") val jobl: List<Combo>
)

@JsonClass(generateAdapter = true)
data class Combo(
    @Json(name = "codigo") val codigo: String,
    @Json(name = "nombre") val nombre: String
)

@JsonClass(generateAdapter = true)
data class JEncuesta(
    @Json(name = "data") val jobl: List<Encuesta>
)

@JsonClass(generateAdapter = true)
data class Encuesta(
    @Json(name = "ENCUESTA_ID") val id: String,
    @Json(name = "ENCUESTA_NOMBRE") val nombre: String,
    @Json(name = "FOTO") val foto: Boolean,
    @Json(name = "PREGUNTA_ID") val pregunta: String,
    @Json(name = "DESCRIP") val descripcion: String,
    @Json(name = "TIPO") val tipo: String,
    @Json(name = "RESPUESTAS") val respuesta: String,
    @Json(name = "FORMATO") val formato: String,
    @Json(name = "CONDICIONAL") val condicional: Boolean,
    @Json(name = "PREVIA") val previa: Int,
    @Json(name = "ELECCION") val eleccion: String
)

@JsonClass(generateAdapter = true)
data class Login(
    @Json(name = "data") val data: DataLogin,
    @Json(name = "token") val token: String
)

@JsonClass(generateAdapter = true)
data class DataLogin(
    @Json(name = "nombre") val nombre: String,
    @Json(name = "tipo") val tipo: Value,
    @Json(name = "empresa") val empresa: Value,
    @Json(name = "empleado") val empleado: DataEmpleado
)

@JsonClass(generateAdapter = true)
data class Value(
    @Json(name = "id") val id: Int,
    @Json(name = "descrip") val descripcion: String
)

@JsonClass(generateAdapter = true)
data class DataEmpleado(
    @Json(name = "sucursal") val sucursal: Int,
    @Json(name = "esquema") val esquema: Int,
    @Json(name = "codigo") val codigo: Int,
    @Json(name = "nombre") val nombre: String
)

//  Pendiente momentaneamente
/*@JsonClass(generateAdapter = true)
data class JRuta(
    @Json(name = "data") val data: List<Ruta>
)

@JsonClass(generateAdapter = true)
data class Ruta(
    @Json(name = "ruta") val ruta: String,
    @Json(name = "coords") val coords: String,
    @Json(name = "XCoord") val longitud: String,
    @Json(name = "YCoord") val latitud: String
)*/

@Parcelize
@JsonClass(generateAdapter = true)
data class ValueName(
    @Json(name = "value") val codigo: Int,
    @Json(name = "name") val descripcion: String
): Parcelable

@JsonClass(generateAdapter = true)
data class JVolumen(
    @Json(name = "data") val jobl: List<Volumen>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Volumen(
    @Json(name = "label") val datos: ValueName,
    @Json(name = "cuota") val cuota: Double,
    @Json(name = "avance") val avance: Double
): Parcelable

@JsonClass(generateAdapter = true)
data class JCobCart(
    @Json(name = "data") val jobl: List<CobCart>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class CobCart(
    @Json(name = "label") val datos: ValueName,
    @Json(name = "cartera") val cartera: Int,
    @Json(name = "avance") val avance: Int
): Parcelable

@JsonClass(generateAdapter = true)
data class JPedido(
    @Json(name = "data") val jobl: List<Pedido>
)

@JsonClass(generateAdapter = true)
data class Pedido(
    @Json(name = "clientes") val cliente: Int,
    @Json(name = "pedidos") val pedido: Int,
    @Json(name = "nuevos") val nuevo: Int,
    @Json(name = "inicio") val inicio: String,
    @Json(name = "ultimo") val ultimo: String
)

@JsonClass(generateAdapter = true)
data class JVisicooler(
    @Json(name = "data") val jobl: List<Visicooler>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Visicooler(
    @Json(name = "cliente") val cliente: ValueName,
    @Json(name = "equipo") val equipo: ValueName,
    @Json(name = "cuota") val cuota: Double,
    @Json(name = "avance") val avance: Double
): Parcelable

@JsonClass(generateAdapter = true)
data class JVisisuper(
    @Json(name = "data") val jobl: List<Visisuper>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Visisuper(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val vendedor: String,
    @Json(name = "clientes") val cliente: Int,
    @Json(name = "avance") val avance: Int
): Parcelable

@JsonClass(generateAdapter = true)
data class JCambio(
    @Json(name = "data") val jobl: List<Cambio>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Cambio(
    @Json(name = "codigo") val codigo: Int,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "cambios") val cambios: Int,
    @Json(name = "monto") val monto: Double
): Parcelable

@JsonClass(generateAdapter = true)
data class JUmes(
    @Json(name = "data") val jobl: List<Umes>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Umes(
    @Json(name = "marca") val marca: ValueName,
    @Json(name = "linea") val linea: ValueName,
    @Json(name = "cuota") val cuota: Double,
    @Json(name = "avance") val avance: Double
) : Parcelable

@JsonClass(generateAdapter = true)
data class JSoles(
    @Json(name = "data") val jobl: List<Soles>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Soles(
    @Json(name = "linea") val linea: ValueName,
    @Json(name = "cuota") val cuota: Double,
    @Json(name = "avance") val avance: Double
) : Parcelable

@JsonClass(generateAdapter = true)
data class JGenerico(
    @Json(name = "data") val jobl: List<Generico>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Generico(
    @Json(name = "label") val datos: ValueName,
    @Json(name = "cuota") val cuota: Double,
    @Json(name = "avance") val avance: Double
): Parcelable

@JsonClass(generateAdapter = true)
data class JCoberturados(
    @Json(name = "data") val jobl: List<Coberturados>
)

@JsonClass(generateAdapter = true)
data class Coberturados(
    @Json(name = "codigo") val codigo: String,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "direccion") val direccion: String,
    @Json(name = "numcuit") val documento: String,
    @Json(name = "XCoord") val longitud: Double,
    @Json(name = "YCoord") val latitud: Double
)

@JsonClass(generateAdapter = true)
data class JPediGen(
    @Json(name = "data") val jobl: List<PediGen>
)

@JsonClass(generateAdapter = true)
data class PediGen(
    @Json(name = "id") val id: String,
    @Json(name = "name") val nombre: String,
    @Json(name = "clientes") val clientes: Int,
    @Json(name = "pedidos") val pedidos: Int,
    @Json(name = "nuevos") val nuevos: Int
)

@JsonClass(generateAdapter = true)
data class Position(
    @Json(name = "lng") val longitud: Double,
    @Json(name = "lat") val latitud: Double
)

@JsonClass(generateAdapter = true)
data class JPedimap(
    @Json(name = "data") val jobl: List<Pedimap>
)

@JsonClass(generateAdapter = true)
data class Pedimap(
    @Json(name = "codigo") val codigo: Int,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "precision") val precision: Double,
    @Json(name = "bateria") val bateria: String,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "hora") val hora: String,
    @Json(name = "estado") val emitiendo: Int,
    @Json(name = "position") val posicion: Position
)

@JsonClass(generateAdapter = true)
data class JBajaVendedor(
    @Json(name = "data") val jobl: List<BajaVendedor>
)

@JsonClass(generateAdapter = true)
data class BajaVendedor(
    @Json(name = "SUCURSAL_ID") val sucursal: Int,
    @Json(name = "EMPLEADO_ID") val empleado: Int,
    @Json(name = "FECHA") val fecha: String,
    @Json(name = "MOTIVO_ID") val motivo: Int,
    @Json(name = "MOTIVO_DESCRIP") val descripcion: String,
    @Json(name = "ESTADO") val estado: String,
    @Json(name = "FECHA_CONFIRMADO") val confirmado: String,
    @Json(name = "CLIENTE_ID") val cliente: Int,
    @Json(name = "CLIENTE_NOMBRE") val nombre: String
)

@JsonClass(generateAdapter = true)
data class JBajaSupervisor(
    @Json(name = "data") val jobl: List<BajaSupervisor>
)

@JsonClass(generateAdapter = true)
data class BajaSupervisor(
    @Json(name = "SUCURSAL_ID") val sucursal: Int,
    @Json(name = "EMPLEADO_ID") val empleado: Int,
    @Json(name = "EMPLEADO_NOMBRE") val nombre: String,
    @Json(name = "FECHA") val creado: String,
    @Json(name = "DIA_SEMANA") val dia: String,
    @Json(name = "MOTIVO_ID") val motivo: String,
    @Json(name = "MOTIVO_DESCRIP") val descripcion: String,
    @Json(name = "OBSERVACION") val observacion: String,
    @Json(name = "CONFIRMADO") val confirmado: Int?,
    @Json(name = "FECHA_CONFIRMADO") val fechaconf: String,
    @Json(name = "CLIENTE") val cliente: ClienteBaja,
    @Json(name = "HORA") val hora: String,
    @Json(name = "PRECISION") val precision: Double,
    @Json(name = "LONGITUD") val longitud: Double,
    @Json(name = "LATITUD") val latitud: Double,
    @Json(name = "ULTIMA_COMPRA") val compra: String
)

@JsonClass(generateAdapter = true)
data class ClienteBaja(
    @Json(name = "ID") val codigo: Int,
    @Json(name = "NOMBRE") val nombre: String,
    @Json(name = "DOCUMENTO") val documento: String,
    @Json(name = "DIRECCION") val direccion: String,
    @Json(name = "RUTA") val ruta: Int,
    @Json(name = "NEGOCIO") val negocio: String,
    @Json(name = "CANAL") val canal: String,
    @Json(name = "PAGO") val pago: String,
    @Json(name = "VISICOOLER") val visicooler: String,
    @Json(name = "LONGITUD") val longitud: Double,
    @Json(name = "LATITUD") val latitud: Double
)