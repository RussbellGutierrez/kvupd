package com.upd.kvupd.viewmodel.state

data class EncuestaState(
    val respuestas: MutableMap<Int, String> = mutableMapOf(),
    var rutaFoto: String = "",
    var encuestaId: Int = 0
)