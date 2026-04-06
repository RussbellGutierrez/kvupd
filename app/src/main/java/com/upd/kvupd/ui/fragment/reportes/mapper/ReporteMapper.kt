package com.upd.kvupd.ui.fragment.reportes.mapper

import com.upd.kvupd.R
import com.upd.kvupd.data.model.Cambio
import com.upd.kvupd.data.model.CoberturaCartera
import com.upd.kvupd.data.model.Coberturados
import com.upd.kvupd.data.model.DetalleCobertura
import com.upd.kvupd.data.model.JsonCambio
import com.upd.kvupd.data.model.JsonCoberturaCartera
import com.upd.kvupd.data.model.JsonCoberturados
import com.upd.kvupd.data.model.JsonDetalleCobertura
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonPedidoGeneral
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.data.model.PedidoGeneral
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.data.model.Volumen
import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte
import com.upd.kvupd.ui.fragment.reportes.modelUI.KpiUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.PedidosRealizados
import com.upd.kvupd.ui.fragment.reportes.modelUI.SolesUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCambioUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCoberturadosUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubDetalleCoberturaUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubPedidoGeneralUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubProgresoUI
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
        return json.jobl
            .sortedBy { it.linea.descripcion.lowercase() }
            .map { it.toLineaUI() }
    }

    private fun Soles.toLineaUI(): LineaUI {
        val cuota = cuota
        val avance = avance
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return LineaUI(
            tipo = TipoReporte.SOLES,
            codigo = linea.codigo,
            titulo = linea.descripcion,
            cuota = "C: ${cuota.to2Dec()}",
            avance = "A: ${avance.to2Dec()}",
            total = "${porcentaje.to2Dec()}%",
            indicador = getIndicador(porcentaje),
            soles = emptyList(),
            isLoading = false
        )
    }

    fun mapVolumenToSoles(json: JsonVolumen): List<SolesUI> {
        return json.jobl
            .sortedBy { it.datos.descripcion.lowercase() }
            .map { it.toSolesUI() }
    }

    private fun Volumen.toSolesUI(): SolesUI {
        val cuota = cuota
        val avance = avance
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return SolesUI(
            id = datos.codigo,
            nombre = datos.descripcion,
            cuota = cuota.to2Dec(),
            avance = avance.to2Dec(),
            total = porcentaje.to2Dec(),
            indicador = getIndicador(porcentaje)
        )
    }

    ///     DETALLE KPI
    fun JsonVolumen.toSubUI(): List<SubProgresoUI> =
        jobl
            .map { it.toSubUI() }
            .sortedBy { it.descripcion }

    private fun Volumen.toSubUI(): SubProgresoUI {
        val porcentaje = if (cuota == 0.0) 0.0 else (avance * 100) / cuota

        return SubProgresoUI(
            codigo = datos.codigo,
            descripcion = datos.descripcion,
            objetivo = "C: ${cuota.to2Dec()}",
            avance = "A: ${avance.to2Dec()}",
            porcentaje = "${porcentaje.to2Dec()}%",
            indicador = getIndicador(porcentaje),
            isLoading = false
        )
    }

    fun JsonCoberturaCartera.toSubUI(): List<SubProgresoUI> =
        jobl
            .map { it.toSubUI() }
            .sortedBy { it.descripcion }

    private fun CoberturaCartera.toSubUI(): SubProgresoUI {
        val cuota = cartera.toDouble()
        val navance = avance.toDouble()
        val porcentaje = if (cuota == 0.0) 0.0 else (navance * 100) / cuota

        return SubProgresoUI(
            codigo = datos.codigo,
            descripcion = datos.descripcion,
            objetivo = "C: ${cuota.to0Dec()}",
            avance = "A: ${navance.to0Dec()}",
            porcentaje = "${porcentaje.to2Dec()}%",
            isLoading = false
        )
    }

    fun JsonDetalleCobertura.toSubUI(): List<SubDetalleCoberturaUI> =
        jobl
            .groupBy { it.codigo }
            .map { (codigo, items) ->
                val nombre = items.first().nombre

                SubDetalleCoberturaUI(
                    codigo = codigo,
                    nombre = nombre,
                    pedidos = items.map { it.toPedido() },
                    isLoading = false
                )
            }.sortedBy { it.nombre }

    private fun DetalleCobertura.toPedido(): PedidosRealizados =
        PedidosRealizados(
            numero = "Ped: $pedido",
            importe = "s/ ${importe.to2Dec()}"
        )

    fun JsonCoberturados.toSubUI(): List<SubCoberturadosUI> =
        jobl
            .map { it.toSubUI() }
            .sortedBy { it.nombre }

    private fun Coberturados.toSubUI(): SubCoberturadosUI =
        SubCoberturadosUI(
            codigo = codigo.toInt(),
            nombre = nombre,
            direccion = direccion,
            documento = "Doc: $documento",
            isLoading = false
        )

    fun JsonPedidoGeneral.toSubUI(): List<SubPedidoGeneralUI> =
        jobl
            .map { it.toSubUI() }
            .sortedBy { it.id }

    private fun PedidoGeneral.toSubUI(): SubPedidoGeneralUI =
        SubPedidoGeneralUI(
            id = id.toInt(),
            nombre = nombre,
            clientes = "Clientes: $clientes",
            pedidos = "Pedidos: $pedidos",
            nuevos = nuevos,
            isLoading = false
        )

    fun JsonCambio.toSubUI(): List<SubCambioUI> =
        jobl
            .map { it.toSubUI() }
            .sortedBy { it.codigo }

    private fun Cambio.toSubUI(): SubCambioUI =
        SubCambioUI(
            codigo = codigo,
            nombre = nombre,
            cambios = "Cambios: $cambios",
            monto = "Monto: ${monto.to2Dec()}",
            isLoading = false
        )

    private fun getIndicador(porcentaje: Double): Int {
        return when {
            porcentaje > 85 -> R.drawable.indicador_verde
            porcentaje in 70.0..85.0 -> R.drawable.indicador_amarillo
            porcentaje in 1.0..69.99 -> R.drawable.indicador_rojo
            else -> R.drawable.indicador_azul
        }
    }
}