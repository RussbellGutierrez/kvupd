package com.upd.kvupd.data.model

object QueryConstant {

    const val GET_SESION = "SELECT * FROM TSesion"
    const val GET_CONFIG = "SELECT * FROM TConfiguracion"
    const val GET_CLIENTES = "SELECT * FROM TClientes"
    const val GET_EMPLEADOS = "SELECT * FROM TEmpleados"
    const val GET_DISTRITOS = "SELECT * FROM TDistrito ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_NEGOCIOS = "SELECT * FROM TNegocio ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_RUTAS = "SELECT * FROM TRutas"
    const val GET_BAJA_SPECIFIC = "SELECT * FROM TBaja WHERE cliente = :cliente"
    const val GET_ALTAS = "SELECT * FROM TAlta ORDER BY fecha DESC"
    const val GET_ALTADATOS = "SELECT * FROM TADatos WHERE idaux = :alta"
    const val GET_BAJA = "SELECT * FROM TBaja ORDER BY fecha DESC"
    const val GET_BAJA_SUPER =
        "SELECT * FROM TBajaSuper WHERE clicodigo = :codigo and creado = :fecha "
    const val GET_SELECCION = "SELECT * FROM TEncuestaSeleccionado"
    const val GET_INCIDENCIA = "SELECT * FROM TIncidencia ORDER BY fecha DESC"

    const val DEL_CONFIG = "DELETE FROM TConfiguracion"
    const val DEL_CLIENTES = "DELETE FROM TClientes"
    const val DEL_EMPLEADOS = "DELETE FROM TEmpleados"
    const val DEL_DISTRITOS = "DELETE FROM TDistrito"
    const val DEL_NEGOCIOS = "DELETE FROM TNegocio"
    const val DEL_RUTAS = "DELETE FROM TRutas"
    const val DEL_ENCUESTA = "DELETE FROM TEncuesta"
    const val DEL_SEGUIMIENTO = "DELETE FROM TSeguimiento"
    const val DEL_VISITA = "DELETE FROM TVisita"
    const val DEL_ESTADO = "DELETE FROM TEstado"
    const val DEL_BAJA = "DELETE FROM TBaja"
    const val DEL_ALTA = "DELETE FROM TAlta"
    const val DEL_ALTADATOS = "DELETE FROM TADatos"
    const val DEL_BAJASUPER = "DELETE FROM TBajaSuper"
    const val DEL_ESTADOBAJA = "DELETE FROM TBEstado"
    const val DEL_SELECCION = "DELETE FROM TEncuestaSeleccionado"
    const val DEL_RESPUESTA = "DELETE FROM TRespuesta"
    const val DEL_INCIDENCIA = "DELETE FROM TIncidencia"

    const val GET_SEGUIMIENTO_SERVER = "SELECT * FROM TSeguimiento " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') ORDER BY fecha ASC"
    const val GET_VISITA_SERVER = "SELECT * FROM TVisita " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') ORDER BY fecha ASC"
    const val GET_ALTA_SERVER = "SELECT * FROM TAlta " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') ORDER BY fecha ASC"
    const val GET_ALTADATO_SERVER = "SELECT * FROM TADatos " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo')"
    const val GET_BAJA_SERVER = "SELECT * FROM TBaja " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') ORDER BY fecha ASC"
    const val GET_BAJAESTADO_SERVER = "SELECT * FROM TBEstado " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') ORDER BY fechaconf ASC"

    const val GET_RESPUESTA_SERVER = "SELECT * FROM TRespuesta " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') AND respuesta != '' ORDER BY fecha ASC"

    const val GET_FOTO_SERVER = "SELECT * FROM TRespuesta " +
            "WHERE ((:estado <> 'Todo' AND estado = :estado) OR :estado = 'Todo') AND foto = 1 ORDER BY fecha ASC"

    const val GET_RESPUESTA_CLIENTE = "" +
            "SELECT encuesta " +
            "FROM TRespuesta " +
            "WHERE cliente = :cliente " +
            "GROUP BY encuesta "

    const val GET_RESPUESTA_HISTORICO = "" +
            "SELECT encuestas " +
            "FROM TClientes " +
            "WHERE idcliente = :cliente "

    const val GET_ROW_CLIENTES = "" +
            "SELECT c.idcliente, c.nomcli, c.empleado, IFNULL(p.descripcion,'null') as descripcion, IFNULL(e.atendido,0) as atendido, c.fecha, c.encuestas, IFNULL(r.encuesta,0) as resuelto, c.secuencia, c.ruta " +
            "FROM TClientes c " +
            "LEFT JOIN TEstado e on c.idcliente=e.idcliente and c.ruta=e.ruta " +
            "LEFT JOIN TEmpleados p on c.empleado=p.codigo " +
            "LEFT JOIN TRespuesta r on c.idcliente=r.cliente " +
            "GROUP BY c.idcliente " +
            "ORDER BY DATE(substr(c.fecha,7,4)||substr(c.fecha,4,2)||substr(c.fecha,1,2)) ASC, c.ruta ASC, c.secuencia ASC, c.idcliente ASC "

    const val GET_LAST_ALTA = "" +
            "SELECT * " +
            "FROM TAlta " +
            "ORDER BY idaux DESC LIMIT 1"

    const val GET_LAST_LOCATION = "" +
            "SELECT * " +
            "FROM TSeguimiento " +
            "ORDER BY fecha DESC LIMIT 1 "

    const val GET_CABE_ENCUESTAS = "" +
            "SELECT distinct e.id, e.nombre, e.foto, IFNULL(s.id,0) as seleccion " +
            "FROM TEncuesta e " +
            "LEFT JOIN TEncuestaSeleccionado s on e.id=s.encuesta "

    const val GET_MARKERS = "" +
            "SELECT c.idcliente, IFNULL(v.longitud,c.longitud) as longitud, IFNULL(v.latitud,c.latitud) as latitud, " +
            "IFNULL(v.observacion,9) as observacion, IFNULL(e.atendido,0) as atendido " +
            "FROM TClientes c " +
            "LEFT JOIN TEstado e on c.idcliente=e.idcliente and c.ruta=e.ruta " +
            "LEFT JOIN TVisita v on c.idcliente=v.cliente " +
            "WHERE ((:observacion <> '9' AND v.observacion = :observacion) OR :observacion = '9') " +
            "ORDER BY c.idcliente ASC "

    const val GET_DATA_CLIENTE = "" +
            "SELECT c.idcliente, c.nomcli, c.domicli, c.ruta, c.negocio, c.telefono,IFNULL(v.observacion,9) as observacion " +
            "FROM TClientes c " +
            "LEFT JOIN TVisita v on c.idcliente=v.cliente " +
            "WHERE ((:cliente <> '0' AND c.idcliente = :cliente) OR :cliente = '0') " +
            "AND ((:observacion <> '9' AND v.observacion = :observacion) OR :observacion = '9') " +
            "ORDER BY c.idcliente ASC "

    const val GET_DATA_ALTA = "" +
            "SELECT a.idaux as idcliente, IFNULL(d.razon,'') ||' '|| IFNULL(d.nombre||' '||d.appaterno||' '||d.apmaterno,'') as nomcli, " +
            "IFNULL(d.via,'') ||' '|| IFNULL(d.direccion,'') ||' '|| IFNULL(d.manzana,'') ||' '|| IFNULL(d.zona,'') ||' '|| " +
            "IFNULL(d.zonanombre,'') ||' '|| IFNULL(d.ubicacion,'') ||' '|| IFNULL(d.numero,'') as domicli," +
            "IFNULL(d.ruta,0) as ruta, IFNULL(d.giro,'') as negocio, IFNULL(d.movil1,'') ||'/'|| IFNULL(d.movil2,'') as telefono " +
            "FROM TAlta a " +
            "LEFT JOIN TADatos d on a.idaux=d.idaux " +
            "WHERE a.idaux = :alta "

    const val GET_ROW_BAJAS = "" +
            "SELECT b.clicodigo as id, b.clinombre as nombre, b.direccion, b.creado as fecha, b.dia, b.descripcion as motivo, b.negocio, IFNULL(e.procede,2) as procede " +
            "FROM TBajaSuper b " +
            "LEFT JOIN TBEstado e on b.clicodigo=e.cliente and b.creado=e.fecha " +
            "WHERE e.procede ISNULL " +
            "ORDER BY creado ASC "

    const val GET_ENCUESTA = "" +
            "SELECT e.id, e.nombre, e.foto, e.pregunta, e.descripcion, e.tipo, e.respuesta, e.formato, e.condicional, e.previa, e.eleccion, e.necesaria " +
            "FROM TEncuesta e " +
            "INNER JOIN TEncuestaSeleccionado s on e.id=s.encuesta " +
            "ORDER BY e.pregunta ASC "
}