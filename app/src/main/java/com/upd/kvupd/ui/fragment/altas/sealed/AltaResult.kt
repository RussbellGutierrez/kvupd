package com.upd.kvupd.ui.fragment.altas.sealed

import java.io.Serializable

sealed class AltaResult : Serializable {
    object Success : AltaResult()
    data class Error(val mensaje: String) : AltaResult()
}