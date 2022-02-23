package com.upd.kv.data.model

object QueryConstant {

    const val GET_CONFIG = "SELECT * FROM TConfiguracion"
    const val GET_CLIENTES = "SELECT * FROM TClientes"
    const val GET_EMPLEADOS = "SELECT * FROM TEmpleados"
    const val GET_DISTRITOS = "SELECT * FROM TDistrito"
    const val GET_NEGOCIOS = "SELECT * FROM TNegocio"
    const val GET_VISITA = "SELECT * FROM TVisita ORDER BY fecha ASC"
    const val GET_BAJA_SPECIFIC = "SELECT * FROM TBaja WHERE cliente = :cliente"

    const val DEL_CLIENTES = "DELETE FROM TClientes"
    const val DEL_EMPLEADOS = "DELETE FROM TEmpleados"
    const val DEL_DISTRITOS = "DELETE FROM TDistrito"
    const val DEL_NEGOCIOS = "DELETE FROM TNegocio"
    const val DEL_ENCUESTA = "DELETE FROM TEncuesta"
    const val DEL_SEGUIMIENTO = "DELETE FROM TSeguimiento"
    const val DEL_VISITA = "DELETE FROM TVisita"
    const val DEL_ESTADO = "DELETE FROM TEstado"
    const val DEL_BAJA = "DELETE FROM TBaja"

    const val GET_ROW_CLIENTES = "" +
            "SELECT c.idcliente, c.nomcli, c.empleado, IFNULL(p.descripcion,'null') as descripcion, IFNULL(e.atendido,0) as atendido, c.fecha, c.encuestas, c.secuencia, c.ruta " +
            "FROM TClientes c " +
            "LEFT JOIN TEstado e on c.idcliente=e.idcliente and c.empleado=e.empleado and c.ruta=e.ruta " +
            "LEFT JOIN TEmpleados p on c.empleado=p.codigo " +
            "ORDER BY DATE(substr(c.fecha,7,4)||substr(c.fecha,4,2)||substr(c.fecha,1,2)) ASC, c.ruta ASC, c.secuencia ASC, c.idcliente ASC "

    const val GET_LAST_LOCATION = "" +
            "SELECT * " +
            "FROM TSeguimiento " +
            "ORDER BY fecha DESC LIMIT 1 "

    const val GET_MARKERS = "" +
            "SELECT c.idcliente, IFNULL(v.longitud,c.longitud) as longitud, IFNULL(v.latitud,c.latitud) as latitud, " +
            "IFNULL(v.observacion,9) as observacion, IFNULL(e.atendido,0) as atendido " +
            "FROM TClientes c " +
            "LEFT JOIN TEstado e on c.idcliente=e.idcliente and c.empleado=e.empleado and c.ruta=e.ruta " +
            "LEFT JOIN TVisita v on c.idcliente=v.cliente " +
            "ORDER BY c.idcliente ASC "

    const val GET_DATA_CLIENTE = "" +
            "SELECT idcliente, nomcli, domicli, ruta, '---' as negocio, '---' as telefono " +
            "FROM TClientes " +
            "WHERE ((:cliente <> '0' AND idcliente = :cliente) OR :cliente = '0') "
}