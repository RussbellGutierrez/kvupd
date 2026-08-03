package com.upd.kvupd.ui.fragment.base.modelUI

import androidx.annotation.DrawableRes

data class BotonInicioUI(
    val texto: String,
    @DrawableRes val icono: Int,
    val visible: Boolean = true
)