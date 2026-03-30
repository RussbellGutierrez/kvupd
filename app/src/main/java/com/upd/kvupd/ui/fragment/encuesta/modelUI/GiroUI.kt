package com.upd.kvupd.ui.fragment.encuesta.modelUI

data class GiroUI(
    val codigo: String,
    val descripcion: String
) {
    override fun toString() = "$codigo - $descripcion"
}