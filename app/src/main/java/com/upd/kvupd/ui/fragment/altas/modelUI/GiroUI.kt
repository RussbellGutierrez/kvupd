package com.upd.kvupd.ui.fragment.altas.modelUI

data class GiroUI(
    val codigo: String,
    val descripcion: String
) {
    override fun toString() = "$codigo - $descripcion"
}