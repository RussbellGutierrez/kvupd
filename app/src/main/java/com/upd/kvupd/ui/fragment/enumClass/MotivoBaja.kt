package com.upd.kvupd.ui.fragment.enumClass

enum class MotivoBaja(val id: Int, val label: String) {
    DUPLICADO(1, "Codigo duplicado"),
    NO_EXISTE(2, "No existe el cliente"),
    CERRADO(3, "Negocio cerrado"),
    CAMBIO_GIRO(4, "Negocio cambio de giro"),
    DESCONOCIDO(0, "Motivo desconocido");

    companion object {

        fun fromId(id: Int): MotivoBaja =
            entries.firstOrNull { it.id == id } ?: DESCONOCIDO

        fun labelFromId(id: Int): String =
            fromId(id).label
    }
}