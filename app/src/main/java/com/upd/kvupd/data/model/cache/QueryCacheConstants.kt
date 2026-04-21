package com.upd.kvupd.data.model.cache

object QueryCacheConstants {

    const val GET_CLIENTES = "SELECT * FROM TableCliente"
    const val GET_VENDEDORES = "SELECT * FROM TableVendedor"
    const val GET_DISTRITOS = "SELECT * FROM TableDistrito ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_NEGOCIOS = "SELECT * FROM TableNegocio ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_RUTAS = "SELECT * FROM TableRuta"
    const val GET_ENCUESTA = "SELECT * FROM TableEncuesta WHERE seleccionada = 1 ORDER BY pregunta ASC"
    const val GET_BAJA_SUPERVISOR = "SELECT * FROM TableBajaSupervisor"

    const val GET_HEADER_ENCUESTAS =
        "SELECT id, nombre, foto, seleccionada FROM TableEncuesta GROUP BY id, nombre, foto ORDER BY id ASC"

    const val UPDATE_CLEAR_ENCUESTA = "UPDATE TableEncuesta SET seleccionada = 0"
    const val UPDATE_SET_SELECCION = "UPDATE TableEncuesta SET seleccionada = 1 WHERE id = :id"
}