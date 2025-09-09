package com.upd.kvupd.data.model

import com.upd.kvupd.utils.OldConstant

fun Configuracion.asTConfig(): TableConfiguracion =
    TableConfiguracion(
        this.codigo,
        this.empresa,
        this.esquema,
        this.fecha,
        this.nombre,
        this.codsuper,
        this.supervisor,
        this.hfin,
        this.hini,
        this.ipp,
        this.ips,
        this.seguimiento,
        this.sucursal,
        this.tipo
    )

fun Cliente.asTCliente(): TableCliente =
    TableCliente(
        this.codigo,
        this.cliente,
        this.ruta,
        this.vendedor,
        this.domicilio,
        this.longitud,
        this.latitud,
        this.telefono,
        this.negocio,
        this.fecha,
        this.secuencia,
        this.numcuit,
        this.encuestas,
        this.ventas,
        this.ventanio
    )

fun Vendedor.asTVendedor(): TableVendedor =
    TableVendedor(this.codigo, this.descripcion, this.cargo)

fun Distrito.asTDistrito(): TableDistrito =
    TableDistrito(this.codigo, this.nombre)

fun Negocio.asTNegocio(): TableNegocio =
    TableNegocio(this.codigo, this.nombre, this.giro, this.descripcion)

fun Encuesta.asTEncuesta(): TableEncuesta =
    TableEncuesta(
        this.id,
        this.nombre,
        this.foto,
        this.pregunta,
        this.descripcion,
        this.tipo,
        this.respuesta,
        this.formato,
        this.condicional,
        this.previa,
        this.eleccion,
        this.necesaria
    )

fun Ruta.asTRutas(): TableRuta =
    TableRuta(
        this.ruta,
        this.coords,
        this.longitud,
        this.latitud,
        this.visita
    )

fun TableBaja.createForTEstado(ruta: Int): TableEstado =
    TableEstado(this.cliente, OldConstant.CONF.codigo, ruta, 2)

fun BajaSupervisor.asTBajaSuper(): TableBajaSupervisor =
    TableBajaSupervisor(
        this.sucursal,
        this.empleado,
        this.nombre,
        this.creado,
        this.dia,
        this.motivo,
        this.descripcion,
        this.observacion,
        this.confirmado,
        this.fechaconf,
        this.cliente.codigo,
        this.cliente.nombre,
        this.cliente.documento,
        this.cliente.direccion,
        this.cliente.ruta,
        this.cliente.negocio,
        this.cliente.canal,
        this.cliente.pago,
        this.cliente.visicooler,
        this.cliente.longitud,
        this.cliente.latitud,
        this.hora,
        this.precision,
        this.longitud,
        this.latitud,
        this.compra
    )

fun Consulta.asTConsulta(): TableConsulta =
    TableConsulta(
        this.cliente,
        this.nombre,
        this.domicilio,
        this.longitud,
        this.latitud,
        this.telefono,
        this.negocio,
        this.canal,
        this.anulado,
        this.documento,
        this.ventas
    )