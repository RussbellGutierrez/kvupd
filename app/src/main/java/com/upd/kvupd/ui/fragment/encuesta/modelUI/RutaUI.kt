package com.upd.kvupd.ui.fragment.encuesta.modelUI

data class RutaUI(
    val codigo: String,
    val dia: Int
) {
    override fun toString(): String {
        val diaTexto = when (dia) {
            2 -> "Lunes"
            3 -> "Martes"
            4 -> "Miércoles"
            5 -> "Jueves"
            6 -> "Viernes"
            7 -> "Sábado"
            else -> ""
        }

        return "$codigo - $diaTexto"
    }
}