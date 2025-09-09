package com.upd.kvupd.ui.sealed

sealed class AppDialogType {
    data class Informativo(
        val titulo: String,
        val mensaje: String,
        val mostrarNegativo: Boolean = false,
        val onPositive: () -> Unit = {},
        val onNegative: (() -> Unit)? = null
    ) : AppDialogType()

    data class Progreso(val mensaje: String) : AppDialogType()
}