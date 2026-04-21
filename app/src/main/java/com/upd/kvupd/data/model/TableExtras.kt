package com.upd.kvupd.data.model

import com.upd.kvupd.R
import com.upd.kvupd.data.model.core.TableConfiguracion

fun TableConfiguracion.nombreEmpresa() =
    if (empresa == 1) "ORIUNDA" else "TERRANORTE"

fun TableConfiguracion.colorSeguimiento() =
    if (seguimiento > 0) R.color.darkgreen else R.color.lightcrimson