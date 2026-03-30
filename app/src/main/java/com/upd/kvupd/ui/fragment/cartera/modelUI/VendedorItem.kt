package com.upd.kvupd.ui.fragment.cartera.modelUI

data class VendedorItem(
    val codigo: String,
    val nombre: String
) {
    override fun toString(): String = "$codigo - $nombre"
}