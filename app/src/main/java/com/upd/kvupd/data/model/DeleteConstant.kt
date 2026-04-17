package com.upd.kvupd.data.model

object DeleteConstant {

    ///     DELETE EN SINCRONIZADO
    const val DEL_CONFIGURACION = "DELETE FROM TableConfiguracion"
    const val DEL_CLIENTES = "DELETE FROM TableCliente"
    const val DEL_VENDEDOR = "DELETE FROM TableVendedor"
    const val DEL_DISTRITOS = "DELETE FROM TableDistrito"
    const val DEL_NEGOCIOS = "DELETE FROM TableNegocio"
    const val DEL_RUTAS = "DELETE FROM TableRuta"
    const val DEL_ENCUESTA = "DELETE FROM TableEncuesta"
    const val DEL_BAJA_SUPERVISOR = "DELETE FROM TableBajaSupervisor"

    ///     DELETE DATOS SERVIDOR
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