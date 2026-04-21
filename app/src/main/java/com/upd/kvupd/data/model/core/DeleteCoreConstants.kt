package com.upd.kvupd.data.model.core

object DeleteCoreConstants {

    /// DELETE CORE
    const val DEL_CONFIGURACION =
        "DELETE FROM TableConfiguracion"

    /// DELETE DATOS SERVIDOR
    const val DEL_SEGUIMIENTO = """
        DELETE FROM TableSeguimiento
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_BAJA = """
        DELETE FROM TableBaja
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_BAJA_PROCESADA = """
        DELETE FROM TableBajaProcesada
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_ALTA = """
        DELETE FROM TableAlta
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_ALTADATOS = """
        DELETE FROM TableAltaDatos
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_RESPUESTA = """
        DELETE FROM TableRespuesta
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val DEL_FOTO = """
        DELETE FROM TableFoto
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """
}