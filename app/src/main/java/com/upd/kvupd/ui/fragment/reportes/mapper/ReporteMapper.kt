package com.upd.kvupd.ui.fragment.reportes.mapper

import com.upd.kvupd.R
import com.upd.kvupd.data.model.JsonCambio
import com.upd.kvupd.data.model.JsonCoberturaCartera
import com.upd.kvupd.data.model.JsonGenerico
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte
import com.upd.kvupd.ui.fragment.reportes.modelUI.KpiUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SolesUI
import com.upd.kvupd.utils.to0Dec
import com.upd.kvupd.utils.to2Dec

object ReporteMapper {

    ///     KPI
    fun mapPreventaKpi(json: JsonVolumen): KpiUI {
        val list = json.jobl

        if (list.isEmpty()) {
            return KpiUI(
                tipo = TipoReporte.PREVENTA,
                isLoading = false,
                isEmpty = true
            )
        }

        val cuota = list.sumOf { it.cuota }
        val avance = list.sumOf { it.avance }
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return KpiUI(
            tipo = TipoReporte.PREVENTA,
            cuota = "C: ${cuota.to2Dec()}",
            avance = "A: ${avance.to2Dec()}",
            total = "${porcentaje.to2Dec()}%",
            isLoading = false
        )
    }

    fun mapCoberturaKpi(json: JsonCoberturaCartera): KpiUI {
        val list = json.jobl

        if (list.isEmpty()) {
            return KpiUI(
                tipo = TipoReporte.COBERTURA,
                isLoading = false,
                isEmpty = true
            )
        }

        val cuota = list.sumOf { it.cartera.toDouble() }
        val avance = list.sumOf { it.avance.toDouble() }
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return KpiUI(
            tipo = TipoReporte.COBERTURA,
            cuota = "C: ${cuota.to0Dec()}",
            avance = "A: ${avance.to0Dec()}",
            total = "${porcentaje.to2Dec()}%",
            isLoading = false
        )
    }

    fun mapCarteraKpi(json: JsonCoberturaCartera): KpiUI {
        val list = json.jobl

        if (list.isEmpty()) {
            return KpiUI(
                tipo = TipoReporte.CARTERA,
                isLoading = false,
                isEmpty = true
            )
        }

        val cuota = list.sumOf { it.cartera.toDouble() }
        val avance = list.sumOf { it.avance.toDouble() }
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return KpiUI(
            tipo = TipoReporte.CARTERA,
            cuota = "C: ${cuota.to0Dec()}",
            avance = "A: ${avance.to0Dec()}",
            total = "${porcentaje.to2Dec()}%",
            isLoading = false
        )
    }

    fun mapPedidosKpi(json: JsonPedido): KpiUI {
        val last = json.jobl.lastOrNull()
            ?: return KpiUI(
                tipo = TipoReporte.PEDIDOS,
                isLoading = false,
                isEmpty = true
            )

        return KpiUI(
            tipo = TipoReporte.PEDIDOS,
            cuota = "Ini: ${last.inicio}",
            avance = "Ult: ${last.ultimo}",
            total = "Pedi: ${last.pedido}",
            isLoading = false
        )
    }

    fun mapCambiosKpi(json: JsonCambio): KpiUI {
        val list = json.jobl

        if (list.isEmpty()) {
            return KpiUI(
                tipo = TipoReporte.CAMBIOS,
                isLoading = false,
                isEmpty = true
            )
        }

        val clientes = list.size
        val cambios = list.sumOf { it.cambios }
        val monto = list.sumOf { it.monto }

        return KpiUI(
            tipo = TipoReporte.CAMBIOS,
            cuota = "Clientes: $clientes",
            avance = "Cambios: $cambios",
            total = "Soles: ${monto.to2Dec()}",
            isLoading = false,
            isEmpty = false
        )
    }

    ///     LINEAS
    fun mapToLineas(json: JsonSoles): List<LineaUI> {

        if (json.jobl.isEmpty()) return emptyList()

        return json.jobl
            .sortedBy { it.linea.descripcion.lowercase() }
            .map { item ->

                val cuota = item.cuota
                val avance = item.avance
                val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

                LineaUI(
                    tipo = TipoReporte.SOLES,
                    codigo = item.linea.codigo,
                    titulo = item.linea.descripcion,
                    cuota = "C: ${cuota.to2Dec()}",
                    avance = "A: ${avance.to2Dec()}",
                    total = "${porcentaje.to2Dec()}%",
                    indicador = getIndicador(porcentaje),
                    soles = emptyList(),
                    isLoading = false // 👈 importante
                )
            }
    }

    ///     PARA VENDEDOR
    fun mapGenericoToSoles(list: JsonGenerico): List<SolesUI> {
        return list.jobl
            .sortedBy { it.datos.descripcion.lowercase() }
            .map { d ->

                val cuota = d.cuota
                val avance = d.avance
                val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

                SolesUI(
                    id = d.datos.codigo,
                    nombre = d.datos.descripcion,
                    cuota = cuota.to2Dec(),
                    avance = avance.to2Dec(),
                    total = porcentaje.to2Dec(),
                    indicador = getIndicador(porcentaje)
                )
            }
    }

    ///     PARA SUPERVISOR
    fun mapVolumenToSoles(list: JsonVolumen): List<SolesUI> {
        return list.jobl
            .sortedBy { it.datos.descripcion.lowercase() }
            .map { d ->

                val cuota = d.cuota
                val avance = d.avance
                val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

                SolesUI(
                    id = d.datos.codigo,
                    nombre = d.datos.descripcion,
                    cuota = cuota.to2Dec(),
                    avance = avance.to2Dec(),
                    total = porcentaje.to2Dec(),
                    indicador = getIndicador(porcentaje)
                )
            }
    }

    private fun getIndicador(porcentaje: Double): Int {
        return when {
            porcentaje > 85 -> R.drawable.indicador_verde
            porcentaje in 70.0..85.0 -> R.drawable.indicador_amarillo
            porcentaje in 1.0..69.99 -> R.drawable.indicador_rojo
            else -> R.drawable.indicador_azul
        }
    }
}