package com.upd.kvupd.ui.fragment.altas.modelUI

data class SubGiroUI(
    val codigo: String,
    val descripcion: String
) {
    override fun toString() = "$codigo - $descripcion"
}