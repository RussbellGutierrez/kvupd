package com.upd.kvupd.ui.fragment.reportes.modelUI

import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte

data class KpiUI(
    val tipo: TipoReporte,
    val cuota: String = "",
    val avance: String = "",
    val total: String = "",
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

data class LineaUI(
    val tipo: TipoReporte,
    val codigo: Int,
    val titulo: String = "",
    val cuota: String = "",
    val avance: String = "",
    val total: String = "",
    val indicador: Int = 0,
    val soles: List<SolesUI> = emptyList(),
    val isLoading: Boolean = true
)

data class SolesUI(
    val id: Int,
    val nombre: String = "",
    val cuota: String = "",
    val avance: String = "",
    val total: String = "",
    val indicador: Int = 0
)