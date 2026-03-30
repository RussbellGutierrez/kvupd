package com.upd.kvupd.ui.fragment.encuesta.modelUI

import com.upd.kvupd.ui.fragment.encuesta.enumFile.TipoPregunta

data class PreguntaUI(
    val idEncuesta: String,
    val pregunta: Int,
    val descripcion: String,
    val tipo: TipoPregunta,
    val opciones: String,
    val esObligatoria: Boolean,
    val tieneFoto: Boolean,
    val previa: Int,
    val eleccion: String
)