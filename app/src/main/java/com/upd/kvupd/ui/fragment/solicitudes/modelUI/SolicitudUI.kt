package com.upd.kvupd.ui.fragment.solicitudes.modelUI

import com.upd.kvupd.ui.fragment.solicitudes.enumFile.TipoSolicitud

data class SolicitudUI(
    val tipo: TipoSolicitud,
    val seleccionado: Boolean = false
)