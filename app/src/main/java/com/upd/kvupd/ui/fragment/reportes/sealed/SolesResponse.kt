package com.upd.kvupd.ui.fragment.reportes.sealed

import com.upd.kvupd.data.model.JsonGenerico
import com.upd.kvupd.data.model.JsonVolumen

sealed class SolesResponse {
    data class Generico(val data: JsonGenerico) : SolesResponse()
    data class Volumen(val data: JsonVolumen) : SolesResponse()
}