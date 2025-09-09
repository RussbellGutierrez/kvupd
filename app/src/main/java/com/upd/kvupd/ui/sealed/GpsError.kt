package com.upd.kvupd.ui.sealed

import com.upd.kvupd.utils.respuestaUsuario

sealed class GpsError : Throwable() {
    object PermisosDenegados : GpsError() {
        override val message = "Los permisos de ubicación están denegados"
    }

    object GpsDesactivado : GpsError() {
        override val message = "El GPS está desactivado"
    }

    data class ErrorDesconocido(val error: Throwable?) : GpsError() {
        override val message = error?.respuestaUsuario() ?: "Error desconocido"
        override val cause = error
    }
}