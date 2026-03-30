package com.upd.kvupd.ui.fragment.encuesta.modelUI

data class ClienteUI(
    val id: String,
    val nombre: String,
    val ventanio: Int
) {
    override fun toString(): String {
        val emoji = if (ventanio == 1) "😵 " else ""
        return "$id - $emoji$nombre"
    }
}