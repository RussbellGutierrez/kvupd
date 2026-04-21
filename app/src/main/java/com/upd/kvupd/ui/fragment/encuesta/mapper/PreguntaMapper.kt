package com.upd.kvupd.ui.fragment.encuesta.mapper

import com.upd.kvupd.data.model.cache.TableEncuesta
import com.upd.kvupd.ui.fragment.encuesta.modelUI.PreguntaUI
import com.upd.kvupd.ui.fragment.encuesta.enumFile.TipoPregunta

fun List<TableEncuesta>.toPreguntaUI(): List<PreguntaUI> {
    return map {
        PreguntaUI(
            idEncuesta = it.id,
            pregunta = it.pregunta,
            descripcion = it.descripcion,
            tipo = when (it.tipo) {
                "U" -> TipoPregunta.UNICA
                "M" -> TipoPregunta.MULTIPLE
                "L" -> TipoPregunta.LIBRE
                else -> TipoPregunta.LIBRE
            },
            opciones = it.respuesta, // lo usarás luego
            esObligatoria = it.necesaria,
            tieneFoto = it.foto,
            previa = it.previa,
            eleccion = it.eleccion
        )
    }
}