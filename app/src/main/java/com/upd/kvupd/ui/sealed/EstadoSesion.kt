package com.upd.kvupd.ui.sealed

sealed class EstadoSesion {
    object Loading : EstadoSesion()
    object Valida : EstadoSesion()
    object Invalida : EstadoSesion()
}