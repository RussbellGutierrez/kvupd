package com.upd.kvupd.ui.fragment.reportes.enumFile

import com.upd.kvupd.domain.enumFile.TipoUsuario

enum class TipoReporte(
    val indice: Int,
    val titulo: String,
    val descripcion: String,
    private val allowedUsers: Set<TipoUsuario>
) {
    PREVENTA(
        1, "Preventa dia", "Versus cuota del día",
        setOf(TipoUsuario.SUPERVISOR)
    ),
    COBERTURA(
        2, "Cobertura clientes", "Versus cobertura del día",
        setOf(TipoUsuario.SUPERVISOR, TipoUsuario.VENDEDOR)
    ),
    CARTERA(
        3, "Clientes no coberturados", "Avance actual",
        setOf(TipoUsuario.SUPERVISOR, TipoUsuario.VENDEDOR)
    ),
    PEDIDOS(
        4, "Pedidos", "Resumen del día",
        setOf(TipoUsuario.SUPERVISOR)
    ),
    CAMBIOS(
        5, "Cambios", "Movimientos registrados",
        setOf(TipoUsuario.SUPERVISOR)
    ),
    SOLES(
        6, "Detalle en soles", "Por línea",
        setOf(TipoUsuario.SUPERVISOR)
    );

    fun canClick(tipoUsuario: TipoUsuario): Boolean {
        return tipoUsuario in allowedUsers
    }

    fun resolveAction(tipoUsuario: TipoUsuario): ReportAction {
        return when (this) {

            PREVENTA -> ReportAction.PREVENTA
            COBERTURA -> {
                if (tipoUsuario == TipoUsuario.SUPERVISOR)
                    ReportAction.COBERTURA_SUP
                else
                    ReportAction.COBERTURA_VEN
            }
            CARTERA -> {
                if (tipoUsuario == TipoUsuario.SUPERVISOR)
                    ReportAction.CARTERA_SUP
                else
                    ReportAction.CARTERA_VEN
            }
            PEDIDOS -> ReportAction.PEDIDOS
            CAMBIOS -> ReportAction.CAMBIOS
            SOLES -> ReportAction.SOLES
        }
    }
}