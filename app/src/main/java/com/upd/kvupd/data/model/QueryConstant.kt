package com.upd.kvupd.data.model

object QueryConstant {

    // Eliminar o revisar las tablas que contengan lo siguiente:
    // TableSeleccionEncuesta
    const val GET_CONFIGURACION = "SELECT * FROM TableConfiguracion"
    const val GET_CLIENTES = "SELECT * FROM TableCliente"
    const val GET_VENDEDORES = "SELECT * FROM TableVendedor"
    const val GET_DISTRITOS = "SELECT * FROM TableDistrito ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_NEGOCIOS = "SELECT * FROM TableNegocio ORDER BY CAST(codigo AS INTEGER) ASC"
    const val GET_RUTAS = "SELECT * FROM TableRuta"
    const val GET_ENCUESTA = """
        SELECT e.id, e.nombre, e.foto, e.pregunta, e.descripcion, e.tipo, e.respuesta,
               e.formato, e.condicional, e.previa, e.eleccion, e.necesaria, e.seleccionada
        FROM TableEncuesta e
        WHERE e.seleccionada = 1
        ORDER BY e.pregunta ASC
    """
    const val GET_BAJA_ESPECIFICA = "SELECT * FROM TableBaja WHERE cliente = :cliente"
    const val GET_INCIDENCIA = "SELECT * FROM TableIncidencia ORDER BY fecha DESC"
    const val GET_ALTAS = "SELECT * FROM TableAlta ORDER BY fecha DESC"

    const val GET_RECYCLER_CLIENTE = """
        SELECT c.idcliente, c.nomcli, c.empleado, IFNULL(v.descripcion,'null') as descripcion, 
               c.fecha, c.encuestas, IFNULL(r.encuesta,0) as resuelto, c.secuencia, c.ruta 
        FROM TableCliente c
        LEFT JOIN TableVendedor v on c.empleado=v.codigo 
        LEFT JOIN TableRespuesta r on c.idcliente=r.cliente 
        GROUP BY c.idcliente 
        ORDER BY DATE(substr(c.fecha,7,4)||substr(c.fecha,4,2)||substr(c.fecha,1,2)) ASC, c.ruta ASC, c.secuencia ASC, c.idcliente ASC
    """

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
    const val ALTADATO_SERVER = """
        SELECT * FROM TableAltaDatos
        WHERE sincronizado = :sync
    """
    const val BAJA_SERVER = """
        SELECT * FROM TableBaja
        WHERE sincronizado = :sync
        ORDER BY fecha ASC
    """
    const val ESTADO_SERVER = """
        SELECT * FROM TableEstado
        WHERE sincronizado = :sync
        ORDER BY fechaconf ASC
    """
    const val RESPUESTA_SERVER = """
        SELECT * FROM TableRespuesta
        WHERE sincronizado = :sync
          AND respuesta != ''
        ORDER BY fecha ASC
    """
    const val FOTO_SERVER = """
        SELECT * FROM TableRespuesta
        WHERE sincronizado = :sync
          AND foto = 1
        ORDER BY fecha ASC
    """


    /***        CAMBIAR O MODIFICAR        ***/

    /*

    const val GET_ALTADATOS = "SELECT * FROM TADatos WHERE idaux = :alta"
    const val GET_BAJA = "SELECT * FROM TBaja ORDER BY fecha DESC"
    const val GET_BAJA_SUPER =
        "SELECT * FROM TBajaSuper WHERE clicodigo = :codigo and creado = :fecha "


    const val GET_RESPUESTA_CLIENTE = "" +
            "SELECT encuesta " +
            "FROM TRespuesta " +
            "WHERE cliente = :cliente " +
            "GROUP BY encuesta "

    const val GET_RESPUESTA_HISTORICO = "" +
            "SELECT encuestas " +
            "FROM TClientes " +
            "WHERE idcliente = :cliente "



    const val GET_LAST_AUX = "" +
            "SELECT * " +
            "FROM TAAux " +
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
            "SELECT c.ventas, c.idcliente, IFNULL(v.longitud,c.longitud) as longitud, IFNULL(v.latitud,c.latitud) as latitud, " +
            "IFNULL(v.observacion,9) as observacion, IFNULL(e.atendido,0) as atendido, c.ventanio " +
            "FROM TClientes c " +
            "LEFT JOIN TEstado e on c.idcliente=e.idcliente AND c.ruta=e.ruta " +
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


    //Tener cuidado con las consultas donde se usan operadores adicionales (LIKE '%dato_consulta%', GLOB '*dato_consulta*')
    const val GET_CONSULTA = "" +
            "SELECT * " +
            "FROM TConsulta " +
            "WHERE ((:numero <> '0' AND (cliente = :numero OR documento = :numero)) OR :numero = '0') " +
            "AND ((:nombre <> 'NOT' AND nombre GLOB :nombre) OR :nombre = 'NOT') "
     */
}