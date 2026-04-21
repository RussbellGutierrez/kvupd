package com.upd.kvupd.data.model.core

object QueryCoreConstants {

    const val GET_CONFIGURACION = "SELECT * FROM TableConfiguracion"
    const val GET_BAJAS_PROCESADAS = "SELECT * FROM TableBajaProcesada"
    const val GET_RESPUESTAS = "SELECT * FROM TableRespuesta"
    const val GET_ALTAS = "SELECT * FROM TableAlta ORDER BY fecha DESC"
    const val GET_BAJAS = "SELECT * FROM TableBaja ORDER BY fecha DESC"
    const val GET_ALTAS_SPECIFIC = "SELECT * FROM TableAlta WHERE idaux = :idaux AND fecha = :fecha"
    const val GET_ALTADATOS = "SELECT * FROM TableAltaDatos WHERE idaux = :idaux AND fecha = :fecha"
    const val GET_LAST_GPS = "SELECT * FROM TableSeguimiento ORDER BY fecha DESC LIMIT 1"

    const val GET_FOTO_LISTA = """
        SELECT rutafoto FROM TableFoto
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    //// TOTAL EN ROOM
    const val SEGUIMIENTO_COUNT = "SELECT COUNT(*) FROM TableSeguimiento"
    const val ALTA_COUNT = "SELECT COUNT(*) FROM TableAlta"
    const val ALTADATO_COUNT = "SELECT COUNT(*) FROM TableAltaDatos"
    const val BAJA_COUNT = "SELECT COUNT(*) FROM TableBaja"
    const val BAJA_PROCESADO_COUNT = "SELECT COUNT(*) FROM TableBajaProcesada"

    const val RESPUESTA_COUNT = """
        SELECT COUNT(*) FROM (
            SELECT cliente, encuesta
            FROM TableRespuesta
            GROUP BY cliente, encuesta
        )
    """

    const val FOTO_COUNT = "SELECT COUNT(*) FROM TableFoto"

    //// SERVER
    const val SEGUIMIENTO_SERVER = """
        SELECT * FROM TableSeguimiento
        WHERE sincronizado = :sync
        ORDER BY fecha ASC
    """

    const val ALTA_SERVER = """
        SELECT * FROM TableAlta
        WHERE sincronizado = :sync
        ORDER BY fecha ASC
    """

    const val ALTA_DATO_SERVER = """
        SELECT * FROM TableAltaDatos
        WHERE sincronizado = :sync
    """

    const val BAJA_SERVER = """
        SELECT * FROM TableBaja
        WHERE sincronizado = :sync
        ORDER BY fecha ASC
    """

    const val BAJA_PROCESADO_SERVER = """
        SELECT * FROM TableBajaProcesada
        WHERE sincronizado = :sync
        ORDER BY fechaconfirmacion ASC
    """

    const val RESPUESTA_SERVER = """
        SELECT * FROM TableRespuesta
        WHERE sincronizado = :sync
        ORDER BY fecha ASC
    """

    const val FOTO_SERVER = """
        SELECT * FROM TableFoto
        WHERE sincronizado = :sync
    """

    //// CONDICIONES
    private const val CONDICION_PENDIENTE = "sincronizado = 0"

    private const val CONDICION_LIMPIEZA = """
        sincronizado = 1
        AND fecha NOT LIKE :hoy || '%'
    """

    //// PENDIENTES
    const val PENDIENTE_SEGUIMIENTO = """
        SELECT EXISTS(
            SELECT 1 FROM TableSeguimiento
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_ALTA = """
        SELECT EXISTS(
            SELECT 1 FROM TableAlta
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_ALTA_DATO = """
        SELECT EXISTS(
            SELECT 1 FROM TableAltaDatos
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_BAJA = """
        SELECT EXISTS(
            SELECT 1 FROM TableBaja
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_BAJA_PROCESADO = """
        SELECT EXISTS(
            SELECT 1 FROM TableBajaProcesada
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_RESPUESTA = """
        SELECT EXISTS(
            SELECT 1 FROM TableRespuesta
            WHERE $CONDICION_PENDIENTE
        )
    """

    const val PENDIENTE_FOTO = """
        SELECT EXISTS(
            SELECT 1 FROM TableFoto
            WHERE $CONDICION_PENDIENTE
        )
    """

    //// LIMPIEZA
    const val LIMPIEZA_SEGUIMIENTO = """
        SELECT EXISTS(
            SELECT 1 FROM TableSeguimiento
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_ALTA = """
        SELECT EXISTS(
            SELECT 1 FROM TableAlta
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_ALTA_DATO = """
        SELECT EXISTS(
            SELECT 1 FROM TableAltaDatos
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_BAJA = """
        SELECT EXISTS(
            SELECT 1 FROM TableBaja
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_BAJA_PROCESADO = """
        SELECT EXISTS(
            SELECT 1 FROM TableBajaProcesada
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_RESPUESTA = """
        SELECT EXISTS(
            SELECT 1 FROM TableRespuesta
            WHERE $CONDICION_LIMPIEZA
        )
    """

    const val LIMPIEZA_FOTO = """
        SELECT EXISTS(
            SELECT 1 FROM TableFoto
            WHERE $CONDICION_LIMPIEZA
        )
    """
}