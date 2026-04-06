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

///     Modelo para detalles
data class SubProgresoUI(
    val codigo: Int,
    val descripcion: String = "",
    val objetivo: String = "",
    val avance: String = "",
    val porcentaje: String = "",
    val indicador: Int = 0,
    val isLoading: Boolean = true
)

data class SubDetalleCoberturaUI(
    val codigo: Int,
    val nombre: String = "",
    val pedidos: List<PedidosRealizados> = emptyList(),
    val isLoading: Boolean = false
)

data class SubCoberturadosUI(
    val codigo: Int,
    val nombre: String = "",
    val direccion: String = "",
    val documento: String = "",
    val isLoading: Boolean = true
)

data class SubPedidoGeneralUI(
    val id: Int,
    val nombre: String = "",
    val clientes: String = "",
    val pedidos: String = "",
    val nuevos: Int = 0,
    val isLoading: Boolean = true
)

data class SubCambioUI(
    val codigo: Int,
    val nombre: String = "",
    val cambios: String = "",
    val monto: String = "",
    val isLoading: Boolean = true
)

data class PedidosRealizados(
    val numero: String,
    val importe: String
)