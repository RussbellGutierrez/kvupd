package com.upd.kvupd.data.model

object QueryConstant {

    const val GET_CONFIGURACION = "SELECT * FROM TableConfiguracion"
    const val GET_CLIENTES = "SELECT * FROM TableCliente"
    const val GET_VENDEDORES = "SELECT * FROM TableVendedor"
    const val GET_DISTRITOS = "SELECT * FROM TableDistrito ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_NEGOCIOS = "SELECT * FROM TableNegocio ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_RUTAS = "SELECT * FROM TableRuta"
    const val GET_ENCUESTA =
        "SELECT * FROM TableEncuesta WHERE seleccionada = 1 ORDER BY pregunta ASC"

    const val GET_ALTAS = "SELECT * FROM TableAlta ORDER BY fecha DESC"
    const val GET_BAJAS = "SELECT * FROM TableBaja ORDER BY fecha DESC"

    const val GET_ALTAS_SPECIFIC = "SELECT * FROM TableAlta WHERE idaux =:idaux AND fecha =:fecha"
    const val GET_ALTADATOS = "SELECT * FROM TableAltaDatos WHERE idaux =:idaux AND fecha =:fecha"

    const val GET_RECYCLER_BAJASUPER = """
        SELECT b.empleado, b.nombre, b.creado, b.motivo, b.clicodigo, b.clinombre, 
               p.procede, b.direccion, b.canal, b.observacion,b.negocio, b.pago, b.compra, 
               b.clilongitud, b.clilatitud
        FROM TableBajaSupervisor b
        LEFT JOIN TableBajaProcesada p on b.empleado=p.empleado AND b.clicodigo=p.cliente
        ORDER BY b.creado DESC
    """
    const val GET_RECYCLER_CLIENTE = """
        SELECT c.idcliente as cliente, c.nomcli, c.empleado as vendedor, IFNULL(v.descripcion,'null') as nomemp, 
               c.domicli, c.longitud, c.latitud, c.ruta, COUNT(b.cliente) as baja, c.ventas, c.ventanio, c.fecha, c.negocio 
        FROM TableCliente c
        LEFT JOIN TableVendedor v on c.empleado=v.codigo 
		LEFT JOIN TableBaja b on c.idcliente=b.cliente
        GROUP BY c.idcliente, c.nomcli, c.empleado, v.descripcion, c.domicli, c.longitud, 
		        c.latitud, c.ruta, c.ventas, c.ventanio, c.fecha, c.negocio  
        ORDER BY c.fecha ASC, c.nomcli ASC
    """
    const val GET_LAST_GPS = "SELECT * FROM TableSeguimiento ORDER BY fecha DESC LIMIT 1"

    const val GET_HEADER_ENCUESTAS =
        "SELECT id, nombre, foto, seleccionada FROM TableEncuesta GROUP BY id, nombre, foto ORDER BY id ASC"

    const val GET_CLIENTES_EXCLUIDOS = """
        SELECT *
        FROM TableCliente c
        WHERE ',' || c.encuestas || ',' NOT LIKE '%,' || :encuestaId || ',%'
        AND NOT EXISTS (
            SELECT 1
            FROM TableRespuesta r
            WHERE r.cliente = c.idcliente
              AND r.encuesta = :encuestaId
        )
    """

    const val GET_FOTO_LISTA = """
        SELECT rutafoto FROM TableFoto
        WHERE fecha NOT LIKE :hoy || '%'
        AND sincronizado = 1
    """

    const val UPDATE_CLEAR_ENCUESTA = "UPDATE TableEncuesta SET seleccionada = 0"
    const val UPDATE_SET_SELECCION = "UPDATE TableEncuesta SET seleccionada = 1 WHERE id = :id"

    ////  TOTAL EN ROOM
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

    ////  SERVER
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

    ////  CONDICIONES
    const val CONDICION_PENDIENTE = "sincronizado = 0"

    const val CONDICION_LIMPIEZA = """
        sincronizado = 1
        AND fecha NOT LIKE :hoy || '%'
    """

    ////  PENDIENTES DE ENVIO
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

    ////  LIMPIEZA DATOS
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