package com.upd.kvupd.ui.fragment.altas.mapper

import com.upd.kvupd.data.model.cache.TableDistrito
import com.upd.kvupd.data.model.cache.TableNegocio
import com.upd.kvupd.data.model.cache.TableRutaProgramacion
import com.upd.kvupd.ui.fragment.altas.modelUI.DistritoUI
import com.upd.kvupd.ui.fragment.altas.modelUI.GiroUI
import com.upd.kvupd.ui.fragment.altas.modelUI.RutaUI
import com.upd.kvupd.ui.fragment.altas.modelUI.SubGiroUI

fun List<TableNegocio>.toGiroUI(): List<GiroUI> {

    val list = this
        .distinctBy { it.giro }
        .map {
            GiroUI(
                codigo = it.giro,
                descripcion = it.descripcion
            )
        }

    return listOf(
        GiroUI(
            codigo = "0",
            descripcion = "NINGUNO"
        )
    ) + list
}

fun List<TableNegocio>.toSubGiroUI(): Map<String, List<SubGiroUI>> {

    val map = groupBy { it.giro }
        .mapValues { (_, lista) ->

            lista.map {

                val descripcion = it.nombre
                    .substringAfter("-")
                    .substringAfter(" ")
                    .trim()

                SubGiroUI(
                    codigo = it.codigo,
                    descripcion = descripcion
                )
            }
        }

    return map + mapOf(
        "0" to listOf(
            SubGiroUI(
                codigo = "0",
                descripcion = "NINGUNO"
            )
        )
    )
}

fun List<TableDistrito>.toDistritoUI(): List<DistritoUI> {
    return map {
        DistritoUI(it.codigo, it.nombre)
    }
}

fun List<TableRutaProgramacion>.toRutaUI(): List<RutaUI> {
    return map {
        RutaUI(
            codigo = it.ruta,
            dia = it.dia
        )
    }
}