package com.upd.kvupd.ui.fragment.encuesta.modelUI

data class EncuestaUI(
    val id: Int,
    val nombre: String,
    val foto: Int,
    val seleccionado: Boolean
) {
    override fun toString(): String {
        val emoji = if (foto == 1) "- 📷" else ""
        return "$id - $nombre $emoji"
    }
}