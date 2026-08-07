package com.upd.kvupd.ui.fragment.cartera.modelUI

data class VendedorItem(
    val codigo: String,
    val nombre: String,
    val cargo: String
) {
    override fun toString(): String = nombre
}