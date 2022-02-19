package com.upd.kv.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class JObj(
    @Json(name = "data") val data: List<JSONObject>
)

@JsonClass(generateAdapter = true)
data class JConfig(
    @Json(name = "data") val data: List<Config>
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
    @Json(name = "data") val data: List<Cliente>
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
    @Json(name = "data") val data: List<Vendedor>
)

@JsonClass(generateAdapter = true)
data class Vendedor(
    @Json(name = "value") val codigo: Int,
    @Json(name = "name") val descripcion: String,
    @Json(name = "type") val cargo: String
)

@JsonClass(generateAdapter = true)  //    Modelado que sirve para distrito y negocio
data class JCombo(
    @Json(name = "data") val data: List<Combo>
)

@JsonClass(generateAdapter = true)
data class Combo(
    @Json(name = "codigo") val codigo: String,
    @Json(name = "nombre") val nombre: String
)

@JsonClass(generateAdapter = true)
data class JEncuesta(
    @Json(name = "data") val data: List<Encuesta>
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