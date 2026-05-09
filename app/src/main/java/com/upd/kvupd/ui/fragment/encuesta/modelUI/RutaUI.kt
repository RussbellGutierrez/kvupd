package com.upd.kvupd.ui.fragment.encuesta.modelUI

import com.upd.kvupd.utils.FechaHoraUtil

data class RutaUI(
    val codigo: String,
    val dia: Int
) {
    override fun toString(): String {
        return "$codigo - ${FechaHoraUtil.diaTexto(dia)}"
    }
}