package com.upd.kvupd.ui.fragment.reportes.modelUI

import android.os.Parcelable
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetalleParams(
    val tipo: TipoReporte,
    val tipoUsuario: TipoUsuario,
    val empleado: String,
    val empresa: String,
    val codigoLinea: Int? = null
) : Parcelable