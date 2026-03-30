package com.upd.kvupd.ui.fragment.base.sealed

sealed class EstadoSesion {
    object Loading : EstadoSesion()
    object Valida : EstadoSesion()
    object Invalida : EstadoSesion()
}