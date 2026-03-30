package com.upd.kvupd.ui.fragment.encuesta.mapper

import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.ui.fragment.encuesta.modelUI.DistritoUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.GiroUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.RutaUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.SubGiroUI

fun List<TableNegocio>.toGiroUI(): List<GiroUI> {
    return this
        .distinctBy { it.giro }
        .map {
            GiroUI(
                codigo = it.giro,
                descripcion = it.descripcion
            )
        }
}

fun List<TableNegocio>.toSubGiroUI(): Map<String, List<SubGiroUI>> {
    return groupBy { it.giro }
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
}

fun List<TableDistrito>.toDistritoUI(): List<DistritoUI> {
    return map {
        DistritoUI(it.codigo, it.nombre)
    }
}

fun List<TableRuta>.toRutaUI(): List<RutaUI> {
    return map {
        RutaUI(
            codigo = it.ruta,
            dia = it.visita.toInt()
        )
    }
}