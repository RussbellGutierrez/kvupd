package com.upd.kvupd.ui.fragment.solicitudes.enumFile

enum class EstadoSolicitud(
    val id: Int,
    val descripcion: String
) {
    PENDIENTE(0, "Pendiente"),
    APROBADO(1, "Aprobado"),
    DENEGADO(2, "Denegado")
}