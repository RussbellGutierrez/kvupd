package com.upd.kvupd.ui.fragment.encuesta.mapper

import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.cache.TableCliente
import com.upd.kvupd.ui.fragment.encuesta.modelUI.ClienteUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.EncuestaUI

fun List<TableCliente>.toClienteUI(): List<ClienteUI> {

    val default = ClienteUI(
        id = "0",
        nombre = "Persona externa al padron",
        ventanio = 0
    )

    return listOf(default) + map {
        ClienteUI(
            id = it.idcliente,
            nombre = it.nomcli,
            ventanio = it.ventanio
        )
    }
}

fun List<FlowHeaderEncuestas>.toEncuestaUI(): List<EncuestaUI> {
    return map {
        EncuestaUI(
            id = it.id,
            nombre = it.encuesta,
            foto = it.conFoto,
            seleccionado = it.seleccionado == 1
        )
    }
}