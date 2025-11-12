package com.upd.kvupd.utils

import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRuta

fun List<TableDistrito>.asSpinner(): List<String> = this.map {
    "${it.codigo} - ${it.nombre}"
}

fun List<TableNegocio>.asSpinner(opt: Int): List<String> = this.map {
    if (opt == 0) {
        "${it.giro} - ${it.descripcion}"
    } else {
        "${it.codigo} - ${it.nombre.replace(Regex("[0-9-]"), "")}"
    }
}

fun List<TableRuta>.toSpinner(): List<String> = this.map {
    val dia = when (it.visita) {
        "1" -> "DO"
        "2" -> "LU"
        "3" -> "MA"
        "4" -> "MI"
        "5" -> "JU"
        "6" -> "VI"
        "7" -> "SA"
        else -> "DF"
    }
    "Ruta $dia ${it.ruta}"
}