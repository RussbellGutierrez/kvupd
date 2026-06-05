package com.upd.kvupd.ui.fragment.solicitudes.enumFile

import com.upd.kvupd.domain.enumFile.TipoUsuario

enum class TipoSolicitud(
    val id: Int,
    val descripcion: String,
    private val allowedUsers: Set<TipoUsuario>
) {

    DESCUENTOS(
        1,
        "Descuentos",
        setOf(
            TipoUsuario.VENDEDOR,
            TipoUsuario.SUPERVISOR
        )
    ),

    BONIFICACIONES(
        2,
        "Bonificaciones",
        setOf(
            TipoUsuario.VENDEDOR,
            TipoUsuario.SUPERVISOR
        )
    ),

    CONDICION_PAGO(
        3,
        "Cambio condicion de pago",
        setOf(
            TipoUsuario.VENDEDOR,
            TipoUsuario.SUPERVISOR
        )
    ),

    COMISIONES(
        4,
        "Comisiones",
        setOf(
            TipoUsuario.SUPERVISOR
        )
    );

    fun canShow(tipoUsuario: TipoUsuario): Boolean {
        return tipoUsuario in allowedUsers
    }

    companion object {

        fun fromId(id: Int): TipoSolicitud? {
            return entries.firstOrNull { it.id == id }
        }

        fun visiblesPara(
            tipoUsuario: TipoUsuario
        ): List<TipoSolicitud> {
            return entries.filter {
                it.canShow(tipoUsuario)
            }
        }
    }
}