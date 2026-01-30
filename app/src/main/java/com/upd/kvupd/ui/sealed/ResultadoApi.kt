package com.upd.kvupd.ui.sealed

import com.upd.kvupd.utils.respuestaUsuario

sealed class ResultadoApi<out T> {

    open fun mensajeUsuario(): String? = null

    object Loading : ResultadoApi<Nothing>()

    data class Exito<out T>(val data: T?) : ResultadoApi<T>()

    data class ErrorHttp(
        val code: Int,
        val mensaje: String?
    ) : ResultadoApi<Nothing>() {
        override fun mensajeUsuario(): String =
            mensaje ?: "Error HTTP $code"
    }

    data class Fallo(
        val throwable: Throwable,
        val mensaje: String = throwable.respuestaUsuario()
    ) : ResultadoApi<Nothing>() {
        override fun mensajeUsuario(): String = mensaje
    }
}