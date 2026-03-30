package com.upd.kvupd.ui.fragment.encuesta.modelUI

data class SubGiroUI(
    val codigo: String,
    val descripcion: String
) {
    override fun toString() = "$codigo - $descripcion"
}