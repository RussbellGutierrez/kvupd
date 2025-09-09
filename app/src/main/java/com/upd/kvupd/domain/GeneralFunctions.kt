package com.upd.kvupd.domain

interface GeneralFunctions {
    fun obtenerIdentificador(): String?
    fun existeIdentificador(): Boolean
    fun crearIdentificador(): String
    fun guardarIdentificador(uuid: String)
    fun crearHash(): String
    fun obtenerNodoDatos(uuid: String): Map<String, String>
}